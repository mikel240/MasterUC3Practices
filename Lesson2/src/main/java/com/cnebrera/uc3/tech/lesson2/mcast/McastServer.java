package com.cnebrera.uc3.tech.lesson2.mcast;

import com.cnebrera.uc3.tech.lesson2.util.VariableSizeMessage;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

/**
 * Multicast server that send messages of variable size into a multicast group
 */
public class McastServer {
    /**
     * Multicast address that we are going to join as receiver
     */
    final static String INET_ADDR = "224.0.0.3";
    /**
     * Port the receiver is going to use
     */
    final static int PORT = 8888;

    public static void main(final String[] args) throws IOException, InterruptedException {
        // Get the address that we are going to connect to.
        final InetAddress address = InetAddress.getByName(INET_ADDR);
        // Open a new DatagramSocket, which will be used to send the data.
        final DatagramSocket serverSocket = new DatagramSocket();

        while (true) {
            sendMessage(address, serverSocket);
        }
    }

    /**
     * Send a message to the given multicast address
     *
     * @param address      the address to send the message into
     * @param serverSocket the server socket that will send the message
     * @throws IOException
     * @throws InterruptedException
     */
    private static void sendMessage(final InetAddress address, final DatagramSocket serverSocket) throws IOException, InterruptedException {
        // Generate random message
        final VariableSizeMessage rndMsg = VariableSizeMessage.generateRandomMsg(8);
        // Convert the message to binary
        final ByteBuffer binaryMessage = rndMsg.toBinary();
        binaryMessage.flip();

        // Create the datagram with the message
        DatagramPacket msgPacket = new DatagramPacket(
                binaryMessage.array(),
                binaryMessage.limit(),
                address,
                PORT
        );

        System.out.println("About to send message datagram of size " + binaryMessage.limit());

        // Send it
        serverSocket.send(msgPacket);
        // Wait a bit
        Thread.sleep(1000);
    }
}
