/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.email.internal;

import java.net.Socket;
import java.util.Map;
import java.util.Properties;

import javax.mail.Session;
import javax.net.SocketFactory;

/**
 * Perform the creation of a persistent set of properties that are used
 * to configure the {@link Session} object in the email connections.
 * <p>
 * The full list of properties can be found at:
 * <ul>
 * <li>POP3 <a>https://javamail.java.net/nonav/docs/api/com/sun/mail/pop3/package-summary.html#properties</a></li>
 * <li>SMTP <a>https://javamail.java.net/nonav/docs/api/com/sun/mail/smtp/package-summary.html#properties</a></li>
 * <li>IMAP <a>https://javamail.java.net/nonav/docs/api/com/sun/mail/imap/package-summary.html#properties</a></li>
 * <ul/>
 *
 * @since 4.0
 */
public class EmailProperties
{

    /**
     * The debug mode to be used.
     */
    public static final String MAIL_DEBUG = "mail.debug";

    /**
     * The host name of the mail server.
     */
    private static final String HOST_PROPERTY = "mail.%s.host";

    /**
     * The port number of the mail server.
     */
    private static final String PORT_PROPERTY = "mail.%s.port";

    /**
     * Socket connection timeout value in milliseconds. Default is infinite timeout.
     */
    private static final String CONNECTION_TIMEOUT_PROPERTY = "mail.%s.connectiontimeout";

    /**
     * Socket write timeout value in milliseconds. Default is infinite timeout.
     */
    private static final String WRITE_TIMEOUT_PROPERTY = "mail.%s.writetimeout";

    /**
     * Indicates if the STARTTLS command shall be used to initiate a TLS-secured connection.
     */
    private static final String START_TLS_PROPERTY = "mail.%s.starttls.enable";

    /**
     * Specifies the {@link SocketFactory} class to create smtp sockets.
     */
    private static final String SOCKET_FACTORY_CLASS_PROPERTY = "mail.%s.socketFactory.class";

    /**
     * Whether to use {@link Socket} as a fallback if the initial connection fails or not.
     */
    private static final String SOCKET_FACTORY_FALLBACK_PROPERTY = "mail.%s.socketFactory.fallback";

    /**
     * Specifies the port to connect to when using a socket factory.
     */
    public static final String SOCKET_FACTORY_PORT = "mail.%s.socketFactory.port";

    /**
     * Specifies the default transport protocol.
     */
    private static final String TRANSPORT_PROTOCOL = "mail.transport.protocol";

    /**
     * Socket read timeout value in milliseconds. This timeout is implemented by {@link Socket}. Default is infinite timeout.
     */
    private static final String TIMEOUT_PROPERTY = "mail.%s.timeout";

    /**
     * Defines the default mime charset to use when none has been specified for the message.
     */
    public static final String MAIL_MIME_CHARSET = "mail.mime.charset";

    /**
     * Default socket set timeout. See JavaMail session properties.
     */
    private static final long READ_TIMEOUT = 15000L;
    /**
     * Default socket connection timeout. See JavaMail session properties.
     */
    private static final long CONNECTION_TIMEOUT = 15000L;

    private final Properties properties;
    private final String protocol;

    /**
     * Creates a new instance and set all the properties required by the specified {@code protocol}.
     */
    private EmailProperties(String protocol, String host, String port, long connectionTimeout, long readTimeout, long writeTimeout)
    {
        this.properties = new Properties();
        this.protocol = protocol;

        set(PORT_PROPERTY, port);
        set(HOST_PROPERTY, host);

        if (isSecure())
        {
            set(START_TLS_PROPERTY, "true");
            set(SOCKET_FACTORY_FALLBACK_PROPERTY, "false");
            //set(SOCKET_FACTORY_CLASS_PROPERTY, "className");
        }

        set(CONNECTION_TIMEOUT_PROPERTY, Long.toString(connectionTimeout < 0L ? CONNECTION_TIMEOUT : readTimeout));
        set(TIMEOUT_PROPERTY, Long.toString(readTimeout < 0L ? READ_TIMEOUT : readTimeout));

        // Note: "mail." + protocol + ".writetimeout" breaks TLS/SSL Dummy Socket and makes tests run 6x slower!!!
        if (writeTimeout > 0L)
        {
            set(WRITE_TIMEOUT_PROPERTY, Long.toString(writeTimeout));
        }

        set(TRANSPORT_PROTOCOL, protocol);
    }


    /**
     * @return true if the protocol is secure, false otherwise.
     */
    private boolean isSecure()
    {
        return protocol.endsWith("s");
    }

    /**
     * Sets a value to the specified property.
     *
     * @param property the property to be set.
     * @param value    the corresponding value for the specified {@code property}
     */
    private void set(String property, String value)
    {
        properties.setProperty(String.format(property, protocol), value);
    }

    /**
     * Configures a new properties instance with the specified parameters.
     *
     * @param protocol   the default protocol to be used.
     * @param host       the host of the mail server.
     * @param port       the port of the mail server.
     * @param properties additional properties.
     * @return a configured properties instance.
     */
    public static Properties get(String protocol, String host, String port, Map<String, String> properties)
    {
        return get(protocol, host, port, CONNECTION_TIMEOUT, READ_TIMEOUT, 0L, properties);
    }

    /**
     * Configures a new properties instance with the specified parameters.
     *
     * @param protocol          the default protocol to be used.
     * @param host              the host of the mail server.
     * @param port              the port of the mail server.
     * @param connectionTimeout the connection timeout.
     * @param readTimeout       the read timeout.
     * @param writeTimeout      the write timeout.
     * @param properties        additional properties.
     * @return a configured properties instance.
     */
    public static Properties get(String protocol, String host, String port, long connectionTimeout, long readTimeout, long writeTimeout, Map<String, String> properties)
    {
        Properties emailProps = new EmailProperties(protocol, host, port, connectionTimeout, readTimeout, writeTimeout).properties;
        if (properties != null && !properties.isEmpty())
        {
            emailProps.putAll(properties);
        }
        return emailProps;
    }
}
