/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.http.internal.request;

import static org.mule.runtime.core.api.config.MuleProperties.SYSTEM_PROPERTY_PREFIX;
import static org.mule.runtime.module.http.api.HttpHeaders.Names.CONTENT_TYPE;
import static org.mule.runtime.module.http.api.HttpHeaders.Names.SET_COOKIE;
import static org.mule.runtime.module.http.api.HttpHeaders.Names.SET_COOKIE2;
import static org.mule.runtime.module.http.api.HttpHeaders.Values.APPLICATION_X_WWW_FORM_URLENCODED;
import org.mule.extension.http.api.HttpResponseAttributes;
import org.mule.extension.http.api.request.HttpRequesterConfig;
import org.mule.extension.http.internal.request.builder.HttpResponseAttributesBuilder;
import org.mule.runtime.api.message.NullPayload;
import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.api.message.MuleMessage;
import org.mule.runtime.core.DefaultMuleMessage;
import org.mule.runtime.core.api.MessagingException;
import org.mule.runtime.core.api.MuleEvent;
import org.mule.runtime.core.transformer.types.MimeTypes;
import org.mule.runtime.core.util.DataTypeUtils;
import org.mule.runtime.core.util.IOUtils;
import org.mule.runtime.core.util.StringUtils;
import org.mule.runtime.module.http.internal.HttpParser;
import org.mule.runtime.module.http.internal.domain.InputStreamHttpEntity;
import org.mule.runtime.module.http.internal.domain.response.HttpResponse;
import org.mule.runtime.module.http.internal.multipart.HttpPartDataSource;

import com.google.common.net.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component that transforms an HTTP response to a proper {@link MuleMessage}.
 *
 * @since 4.0
 */
public class HttpResponseToMuleMessage
{
    private static final Logger logger = LoggerFactory.getLogger(HttpResponseToMuleMessage.class);
    private static final String MULTI_PART_PREFIX = "multipart/";

    private final Boolean parseResponse;
    private final HttpRequesterConfig config;

    public HttpResponseToMuleMessage(HttpRequesterConfig config, Boolean parseResponse)
    {
        this.config = config;
        this.parseResponse = parseResponse;
    }

    public MuleMessage<Object, HttpResponseAttributes> convert(MuleEvent muleEvent, HttpResponse response, String uri) throws MessagingException
    {
        String responseContentType = response.getHeaderValue(CONTENT_TYPE.toLowerCase());
        DataType dataType = muleEvent.getMessage().getDataType();
        if (StringUtils.isEmpty(responseContentType) && !MimeTypes.ANY.equals(dataType.getMimeType()))
        {
            responseContentType = DataTypeUtils.getContentType(dataType);
        }

        InputStream responseInputStream = ((InputStreamHttpEntity) response.getEntity()).getInputStream();
        String encoding = getEncoding(responseContentType);

        Object payload = responseInputStream;
        Map<String, DataHandler> parts = new HashMap<>();
        if (responseContentType != null && parseResponse)
        {
            if (responseContentType.startsWith(MULTI_PART_PREFIX))
            {
                try
                {
                    parts = processParts(responseInputStream, responseContentType);
                    payload = NullPayload.getInstance();
                }
                catch (IOException e)
                {
                    throw new MessagingException(muleEvent, e);
                }
            }
            else if (responseContentType.startsWith(APPLICATION_X_WWW_FORM_URLENCODED.toLowerCase()))
            {
                payload = HttpParser.decodeString(IOUtils.toString(responseInputStream), encoding);
            }
        }

        if (config.isEnableCookies())
        {
            processCookies(response, uri);
        }

        HttpResponseAttributes responseAttributes = createAttributes(response, parts);
        MuleMessage responseMessage = new DefaultMuleMessage(payload, dataType, responseAttributes);

        String requestMessageId = muleEvent.getMessage().getUniqueId();
        String requestMessageRootId = muleEvent.getMessage().getMessageRootId();

        // Setting uniqueId and rootId in order to correlate messages from request to response generated.
        ((DefaultMuleMessage) responseMessage).setUniqueId(requestMessageId);
        ((DefaultMuleMessage) responseMessage).setMessageRootId(requestMessageRootId);

        if (encoding != null)
        {
            responseMessage.getDataType().setEncoding(encoding);
        }

        return responseMessage;
    }

    private HttpResponseAttributes createAttributes(HttpResponse response, Map<String, DataHandler> parts)
    {
        return new HttpResponseAttributesBuilder().setResponse(response).setParts(parts).build();
    }

    private String getEncoding(String responseContentType)
    {
        String encoding = Charset.defaultCharset().name();

        if (responseContentType != null)
        {
            try
            {
                MediaType mediaType = MediaType.parse(responseContentType);
                if (mediaType.charset().isPresent())
                {
                    encoding = mediaType.charset().get().name();
                }
            }
            catch (IllegalArgumentException e)
            {
                if (Boolean.parseBoolean(System.getProperty(SYSTEM_PROPERTY_PREFIX + "strictContentType")))
                {
                    throw e;
                }
                else
                {
                    logger.warn(String.format("%s when parsing Content-Type '%s': %s", e.getClass().getName(), responseContentType, e.getMessage()));
                    logger.warn(String.format("Using default encoding: %s", encoding));
                }
            }
        }

        return encoding;
    }

    private Map<String, DataHandler> processParts(InputStream responseInputStream, String responseContentType) throws IOException
    {
        Collection<HttpPartDataSource> httpParts = HttpPartDataSource.createFrom(HttpParser.parseMultipartContent(responseInputStream, responseContentType));
        Map<String, DataHandler> attachments = new HashMap<>();

        for (HttpPartDataSource httpPart : httpParts)
        {
            attachments.put(httpPart.getName(), new DataHandler(httpPart));
        }

        return attachments;
    }

    private void processCookies(HttpResponse response, String uri)
    {
        Collection<String> setCookieHeader = response.getHeaderValues(SET_COOKIE.toLowerCase());
        Collection<String> setCookie2Header = response.getHeaderValues(SET_COOKIE2.toLowerCase());

        Map<String, List<String>> cookieHeaders = new HashMap<>();

        if (setCookieHeader != null)
        {
            cookieHeaders.put(SET_COOKIE, new ArrayList<>(setCookieHeader));
        }

        if (setCookie2Header != null)
        {
            cookieHeaders.put(SET_COOKIE2, new ArrayList<>(setCookie2Header));
        }

        try
        {
            config.getCookieManager().put(URI.create(uri), cookieHeaders);
        }
        catch (IOException e)
        {
            logger.warn("Error storing cookies for URI " + uri, e);
        }
    }
}
