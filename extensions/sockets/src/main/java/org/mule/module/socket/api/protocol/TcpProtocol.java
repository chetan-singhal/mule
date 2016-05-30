/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.protocol;

import org.mule.runtime.core.api.serialization.ObjectSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface TcpProtocol
{
    /**
     * Reads the input stream and returns a whole message.
     *
     * @param is the input stream
     * @return an array of byte containing a full message
     * @throws IOException if an exception occurs
     */
    Object read(InputStream is) throws IOException;

    /**
     * Write the specified message to the output stream.
     *
     * @param os the output stream to write to
     * @param data the data to write
     * @throws IOException if an exception occurs
     */
    void write(OutputStream os, Object data) throws IOException;

    InputStream getInputStreamWrapper(InputStream inputStream);

    boolean getRethrowExceptionOnRead();

    void setObjectSerializer(ObjectSerializer serializer);
}
