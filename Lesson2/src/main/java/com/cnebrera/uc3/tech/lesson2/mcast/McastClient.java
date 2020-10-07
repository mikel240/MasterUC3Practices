package com.cnebrera.uc3.tech.lesson2.mcast;

import com.cnebrera.uc3.tech.lesson2.util.VariableSizeMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * Multicast client that read messages of variable size from a server
 */
public class McastClient {
    /**
     * Multicast address that we are going to join as receiver
     */
    final static String INET_ADDR = "224.0.0.3";
    /**
     * Port the receiver is going to use
     */
    final static int PORT = 8888;

    public static void main(String[] args) throws UnknownHostException, IOException {
        // Get the address that we are going to connect to.
        final InetAddress address = InetAddress.getByName(INET_ADDR);
        // Create a buffer of bytes, which will be used to store the received messages
        final ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);

        // Create a new multicast socket
        final MulticastSocket clientSocket = new MulticastSocket(PORT);
        // Joint the Multicast group given by the multicast address
        clientSocket.joinGroup(address);

        while (true) {
            // Call receiveMessage with the buffer and the socket
            receiveMessage(receiveBuffer, clientSocket);
        }
    }

    /**
     * Receive the next datagram for the given multicast socket
     *
     * @param receiveBuffer the reusable buffer that will be used to store the message binary contents
     * @param clientSocket  the multicast client socket
     * @throws IOException exception thrown if there is a problem in the input/output
     */
    private static void receiveMessage(final ByteBuffer receiveBuffer, final MulticastSocket clientSocket)
            throws IOException {
        // Clear the receive buffer
        receiveBuffer.clear();

        // Create a datagram to receive the next message
        final DatagramPacket msgPacket = new DatagramPacket(
                receiveBuffer.array(),
                receiveBuffer.capacity()
        );

        // Receive the next message
        clientSocket.receive(msgPacket);

        // Set the limits of the buffer with the received datagram information
        receiveBuffer.limit(msgPacket.getLength());

        // Deserialize the message
        final VariableSizeMessage msg = VariableSizeMessage.readMsgFromBinary(receiveBuffer.limit(), receiveBuffer);
        System.out.println("Msg received " + msg);
    }
}
