/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
///*
// * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
// * The software in this package is published under the terms of the CPAL v1.0
// * license, a copy of which has been included with this distribution in the
// * LICENSE.txt file.
// */
//package org.mule.module.socket.api.client;
//
//import static org.mule.module.socket.internal.SocketUtils.getSocketAddressbyName;
//import org.mule.module.socket.api.exceptions.UnresolvableHostException;
//import org.mule.module.socket.api.udp.UdpSocketProperties;
//import org.mule.runtime.api.connection.ConnectionException;
//import org.mule.runtime.core.api.serialization.ObjectSerializer;
//
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//
//public abstract class AbstractUdpClient extends AbstractSocketClient
//{
//
//    protected DatagramSocket socket;
//    protected UdpSocketProperties socketProperties;
//    protected InetAddress address;
//
//    protected ObjectSerializer objectSerializer;
//
//    public AbstractUdpClient(UdpSocketProperties socketProperties, String host, Integer port) throws ConnectionException
//    {
//        super(host, port);
//        this.socketProperties = socketProperties;
//    }
//
//    protected void initialise() throws ConnectionException
//    {
//        try
//        {
//            if (socket == null)
//            {
//                throw new IllegalStateException("Socket must be created before being configured");
//            }
//
//            socket.setSendBufferSize(socketProperties.getSendBufferSize());
//            socket.setReceiveBufferSize(socketProperties.getReceiveBufferSize());
//            socket.setBroadcast(socketProperties.getBroadcast());
//            socket.setSoTimeout(socketProperties.getTimeout());
//            socket.setReuseAddress(socketProperties.getReuseAddress());
//            address = getSocketAddressbyName(host);
//        }
//        catch (Exception e)
//        {
//            throw new ConnectionException("UDP Socket could not be created", e);
//        }
//    }
//
//    public void setObjectSerializer(ObjectSerializer objectSerializer)
//    {
//        this.objectSerializer = objectSerializer;
//    }
//
//    /**
//     * Creates a {@link DatagramPacket} with the size of the content, addressed to
//     * the port and address of the client.
//     * @param content that is going to be sent inside the packet
//     * @return a packet ready to be sent
//     * @throws UnresolvableHostException
//     */
//    protected DatagramPacket createPacket(byte[] content) throws UnresolvableHostException
//    {
//        DatagramPacket packet = new DatagramPacket(content, content.length);
//        packet.setAddress(address);
//        packet.setPort(port);
//        return packet;
//    }
//
//    /**
//     *
//     * @return a packet configured to be used for receiving purposes
//     * @throws UnresolvableHostException
//     */
//    protected DatagramPacket createPacket() throws UnresolvableHostException
//    {
//        int bufferSize = socketProperties.getReceiveBufferSize();
//        DatagramPacket packet = new DatagramPacket(new byte[bufferSize], bufferSize);
//        return packet;
//    }
//}