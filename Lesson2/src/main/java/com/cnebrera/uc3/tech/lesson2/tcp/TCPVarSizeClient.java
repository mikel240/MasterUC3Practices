package com.cnebrera.uc3.tech.lesson2.tcp;

import com.cnebrera.uc3.tech.lesson2.util.VariableSizeMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * TCP client that read messages of variable size from a server
 */
public class TCPVarSizeClient {
    public static void main(String argv[]) throws Exception {
        // Create the client socket
        final Socket clientSocket = new Socket("localhost", 6789);
        // Get the input stream
        final InputStream inputStream = clientSocket.getInputStream();

        while (true) {
            readMessages(inputStream);
        }
    }

    /**
     * Send messages into the input stream
     *
     * @param inputStream the input stream connected to the socket
     * @throws IOException exception if there is an input output problem
     */
    private static void readMessages(final InputStream inputStream) throws IOException {
        // The buffer to read the header
        final byte[] header = new byte[4];

        // We need to have enough bytes in the stream to contain the whole message
        while (inputStream.available() < 4) ;

        // Read the header
        inputStream.read(header);
        final int msgSize = ByteBuffer.wrap(header).getInt();
        System.out.println("Read MsgSize " + msgSize);

        // The buffer to read the message bytes
        final byte[] msgBytes = new byte[msgSize];

        // Wait for the whole message to be ready
        while (inputStream.available() < msgBytes.length) ;
        // Read the message bytes
        inputStream.read(msgBytes);

        // Create the message
        final VariableSizeMessage msg = VariableSizeMessage.readMsgFromBinary(msgSize, ByteBuffer.wrap(msgBytes));
        System.out.println("Msg received " + msg);
    }
}
