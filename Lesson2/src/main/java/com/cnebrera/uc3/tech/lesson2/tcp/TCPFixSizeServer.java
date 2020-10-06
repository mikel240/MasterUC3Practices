package com.cnebrera.uc3.tech.lesson2.tcp;

import com.cnebrera.uc3.tech.lesson2.util.FixSizeMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * TCP server that send messages of fixed size to a client
 */
public class TCPFixSizeServer {
    public static void main(String argv[]) throws Exception {
        // Create a socket
        final ServerSocket welcomeSocket = new ServerSocket(6789);
        // Accept connection
        final Socket connectionSocket = welcomeSocket.accept();
        // Call sendMessagesToClient
        sendMessagesToClient(connectionSocket);
    }

    /**
     * Send fix size messages to a connected socket client
     *
     * @param connectionSocket the client connection socket
     * @throws IOException          exception thrown if there is an IO problem
     * @throws InterruptedException exception thrown if interrupted
     */
    private static void sendMessagesToClient(final Socket connectionSocket)
            throws IOException, InterruptedException {
        final OutputStream outputStream = connectionSocket.getOutputStream();
        // The buffer to write the
        final ByteBuffer sendBuffer = ByteBuffer.allocate(FixSizeMessage.MSG_BINARY_SIZE);

        while (true) {
            // Clear the buffer
            sendBuffer.clear();
            // Generate random message
            final FixSizeMessage rndMsg = FixSizeMessage.generateRandomMsg();
            // Convert to binary
            rndMsg.toBinary(sendBuffer);

            // Write the contents in the output stream
            outputStream.write(sendBuffer.array());
            // Flush to force the message to be send without batching
            outputStream.flush();

            System.out.println("Message sent: " + rndMsg.toString());
            // Wait a bit before sending the next message
            Thread.sleep(1000);
        }
    }
}
