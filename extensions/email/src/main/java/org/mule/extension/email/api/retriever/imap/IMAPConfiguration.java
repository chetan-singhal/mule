/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.email.api.retriever.imap;

import static org.mule.extension.email.internal.util.EmailConstants.DEFAULT_FOLDER;
import org.mule.extension.email.api.retriever.RetrieverConfiguration;
import org.mule.runtime.extension.api.annotation.Configuration;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.Parameter;
import org.mule.runtime.extension.api.annotation.connector.Providers;
import org.mule.runtime.extension.api.annotation.param.Optional;

/**
 *  Configuration for operations that are performed through the IMAP
 *  protocol.
 *
 * @since 4.0
 */
@Operations(IMAPOperations.class)
@Providers(IMAPConnectionProvider.class)
@Configuration(name = "imap")
public class IMAPConfiguration implements RetrieverConfiguration
{

    /**
     * The folder from which emails are going to be
     * retrieved. The default one is the "INBOX" folder.
     */
    @Parameter
    @Optional(defaultValue = DEFAULT_FOLDER)
    private String folder;

    /**
     * Indicates whether the retrieved emails should be opened
     * and read. The default value is {@code true}.
     */
    @Parameter
    @Optional(defaultValue = "true")
    private boolean readContent;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFolder()
    {
        return folder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean shouldReadContent()
    {
        return readContent;
    }
}
