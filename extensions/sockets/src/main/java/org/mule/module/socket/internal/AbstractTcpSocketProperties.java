/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.internal;

import org.mule.module.socket.api.tcp.TcpSocketProperties;
import org.mule.runtime.extension.api.annotation.Parameter;
import org.mule.runtime.extension.api.annotation.param.Optional;

/**
 * Mutable base class for implementations of {@link TcpSocketProperties}
 *
 * @since 4.0
 */
public abstract class AbstractTcpSocketProperties extends AbstractSocketProperties implements TcpSocketProperties
{

    /**
     * If set, transmitted data is not collected together for greater efficiency but sent immediately.
     * <p>
     * Defaults to {@code true} even though Socket default is false because optimizing to reduce amount of network
     * traffic over latency is hardly ever a concern today.
     */
    @Parameter
    @Optional(defaultValue = "true")
    protected boolean sendTcpNoDelay = true;


    /**
     * This sets the SO_LINGER value. This is related to how long (in milliseconds) the socket will take to close so
     * that any remaining data is transmitted correctly.
     */
    @Parameter
    @Optional
    protected Integer linger;

    /**
     * Enables SO_KEEPALIVE behavior on open sockets. This automatically checks socket connections that are open but
     * unused for long periods and closes them if the connection becomes unavailable.
     * <p>
     * This is a property on the socket itself and is used by a server socket to control whether connections to the
     * server are kept alive before they are recycled.
     */
    @Parameter
    @Optional
    protected Boolean keepAlive;


    /**
     * Whether the socket should fail during its creation if the host set on the endpoint cannot be resolved.
     * However, it can be set to false to allow unresolved hosts (useful when connecting through a proxy).
     */
    @Parameter
    @Optional(defaultValue = "true")
    protected boolean failOnUnresolvedHost = true;

    public Integer getLinger()
    {
        return linger;
    }

    public Boolean getKeepAlive()
    {
        return keepAlive;
    }

    @Override
    public int getTimeout()
    {
        return timeout;
    }

    public Boolean getSendTcpNoDelay()
    {
        return sendTcpNoDelay;
    }

    public boolean getFailOnUnresolvedHost()
    {
        return failOnUnresolvedHost;
    }
}
