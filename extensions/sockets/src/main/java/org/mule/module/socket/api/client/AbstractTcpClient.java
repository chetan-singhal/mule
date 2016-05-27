/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.client;

import org.mule.module.socket.api.protocol.TcpProtocol;
import org.mule.runtime.api.connection.ConnectionException;

public abstract class AbstractTcpClient extends AbstractSocketClient
{
    final TcpProtocol protocol;

    public AbstractTcpClient(TcpProtocol protocol, String host, int port)
    {
        super(host, port);
        this.protocol = protocol;
    }

    protected abstract void initialise() throws ConnectionException;
}
