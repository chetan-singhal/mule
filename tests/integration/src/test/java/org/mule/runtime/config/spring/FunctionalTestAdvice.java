/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.config.spring;

import org.mule.runtime.core.api.MuleEventContext;
import org.mule.runtime.core.util.concurrent.Latch;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.springframework.aop.MethodBeforeAdvice;

public class FunctionalTestAdvice implements MethodBeforeAdvice
{

    private Latch latch = new Latch();
    private String message;

    public void before(Method method, Object[] args, Object target) throws Throwable
    {
        if (null != args && args.length == 1 && args[0] instanceof MuleEventContext)
        {
            message = ((MuleEventContext) args[0]).getMessageAsString();
        }
        latch.countDown();
    }

    public String getMessage(long ms) throws InterruptedException
    {
        latch.await(ms, TimeUnit.MILLISECONDS);
        return message;
    }

}
