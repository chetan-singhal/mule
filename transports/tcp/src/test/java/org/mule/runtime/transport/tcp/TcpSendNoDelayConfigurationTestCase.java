/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.transport.tcp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.runtime.transport.tcp.TcpConnector;

import org.junit.Test;

public class TcpSendNoDelayConfigurationTestCase extends FunctionalTestCase
{
    @Override
    protected String getConfigFile()
    {
        return "send-tcp-no-delay-configuration-test.xml";
    }

    @Test
    public void tcpNoDelay() throws Exception
    {
        assertEquals(getDefaultSendTcpNoDelay(), lookupConnector("tcpConnector").isSendTcpNoDelay());
    }

    @Test
    public void tcpNoDelayTrue() throws Exception
    {
        assertTrue(lookupConnector("tcpConnectorSendTcpNoDelayTrue").isSendTcpNoDelay());
    }

    @Test
    public void tcpNoDelayFalse() throws Exception
    {
        assertFalse(lookupConnector("tcpConnectorSendTcpNoDelayFalse").isSendTcpNoDelay());
    }

    protected TcpConnector lookupConnector(String name)
    {
        return (TcpConnector) muleContext.getRegistry().lookupObject(name);
    }

    protected boolean getDefaultSendTcpNoDelay()
    {
        return false;
    }

}
