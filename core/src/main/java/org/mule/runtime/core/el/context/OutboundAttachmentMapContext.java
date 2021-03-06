/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.el.context;

import org.mule.runtime.core.api.MuleEvent;
import org.mule.runtime.core.api.MuleRuntimeException;

import java.util.Set;

import javax.activation.DataHandler;

public class OutboundAttachmentMapContext extends AbstractMapContext<DataHandler>
{
    private MuleEvent event;

    public OutboundAttachmentMapContext(MuleEvent event)
    {
        this.event = event;
    }

    @Override
    public DataHandler doGet(String key)
    {
        return event.getMessage().getOutboundAttachment(key);
    }

    @Override
    public void doPut(String key, DataHandler value)
    {
        try
        {
            event.getMessage().addOutboundAttachment(key, value);
        }
        catch (Exception e)
        {
            throw new MuleRuntimeException(e);
        }
    }

    @Override
    public void doRemove(String key)
    {
        try
        {
            event.getMessage().removeOutboundAttachment((String) key);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> keySet()
    {
        return event.getMessage().getOutboundAttachmentNames();
    }
    
    @Override
    public void clear()
    {
        event.getMessage().clearAttachments();
    }

}
