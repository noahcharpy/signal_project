package com.data_management;

import java.net.URI;

/**
 * WebSocketDataReader is a DataReader implementation that streams
 * real-time patient data from a WebSocket server.
 * <p>
 * This class manages the lifecycle of the {@link WebSocketClient},
 * including starting and stopping the connection.
 */
public class WebSocketDataReader implements DataReader {

    private WebSocketClient client;

    /**
     * Use {@link #startStreaming(DataStorage)} instead.
     *
     * @param storage the data storage object
     * @throws UnsupportedOperationException
     */
    @Override
    public void readData(DataStorage storage) {
        throw new UnsupportedOperationException("not supported for WebSocketDataReader");
    }

    /**
     * Starts streaming real-time data from the WebSocket server.
     * Connects to the server and begins receiving messages, which are processed and stored in the provided {@link DataStorage}.
     *
     * @param storage the data storage where incoming data will be saved
     */
    @Override
    public void startStreaming(DataStorage storage) {
        try {
            URI serverUri = new URI("ws://localhost:1234");  // Replace with your actual WebSocket server address
            client = new WebSocketClient(serverUri, storage);
            client.connect();

            // Wait until the connection is actually established (max 5 seconds)
            int attempts = 0;
            while (!client.isConnected() && attempts < 10) {
                Thread.sleep(500);
                attempts++;
            }

            if (client.isConnected()) {
                System.out.println("WebSocketDataReader connected successfully.");
            } else {
                System.err.println("WebSocketDataReader failed to connect within timeout.");
            }
            System.out.println("WebSocketDataReader started streaming...");

        } catch (Exception e) {
            System.err.println("Failed to start WebSocket streaming: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isClientOpen() {
        return client != null && client.isOpen();
    }

    /**
     * Stops the WebSocket streaming session if it's open
     */
    public void stopStreaming() {
        if (client != null && client.isOpen()) {
            client.close();
            System.out.println("WebSocketDataReader stopped streaming.");
        }
    }
}
