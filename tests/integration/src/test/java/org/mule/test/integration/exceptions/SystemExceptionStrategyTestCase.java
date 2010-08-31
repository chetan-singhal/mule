/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.integration.exceptions;

import org.mule.api.lifecycle.LifecycleException;
import org.mule.tck.testmodels.mule.TestConnector;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

public class SystemExceptionStrategyTestCase extends AbstractExceptionStrategyTestCase
{
    @Override
    protected String getConfigResources()
    {
        return "org/mule/test/integration/exceptions/system-exception-strategy.xml";
    }

    public void testConnectorStartup() throws Exception
    {
        try
        {
            TestConnector c = (TestConnector) muleContext.getRegistry().lookupConnector("testConnector");
            c.setInitialStateStopped(false);
            c.start();
            fail("Connector should have thrown an exception");
        }
        catch (LifecycleException e)
        {
            // expected
        }
        latch.await(1000, TimeUnit.MILLISECONDS); 
        assertEquals(0, serviceExceptionCounter.get());
        assertEquals(1, systemExceptionCounter.get());
    }

    public void testConnectorPolling() throws Exception
    {
        muleContext.getRegistry().lookupService("Polling").start();
        Thread.sleep(3000);
        latch.await(1000, TimeUnit.MILLISECONDS); 
        assertEquals(0, serviceExceptionCounter.get());
        assertEquals(1, systemExceptionCounter.get());
    }
}


