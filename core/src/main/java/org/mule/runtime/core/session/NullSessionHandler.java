/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.session;

import org.mule.runtime.core.api.MuleException;
import org.mule.runtime.core.api.MuleMessage;
import org.mule.runtime.core.api.MuleSession;
import org.mule.runtime.core.message.SessionHandler;

/**
 * A session handler that ignores any session information
 */
public class NullSessionHandler implements SessionHandler
{
    public MuleSession retrieveSessionInfoFromMessage(MuleMessage message) throws MuleException
    {
        return null;
    }

    public void storeSessionInfoToMessage(MuleSession session, MuleMessage message) throws MuleException
    {
        // noop
    }

    /**
     * The property name of the session id to use when creating the Mule session. by
     * default the property name "ID" will be used. If no property was set on the
     * session called "ID" a session id will be automatically generated
     * 
     * @return the property name of the session id that is set on the session
     */
    public String getSessionIDKey()
    {
        return "ID";
    }
}
