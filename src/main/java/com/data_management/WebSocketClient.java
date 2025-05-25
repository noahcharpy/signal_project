package com.data_management;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * WebSocketClient connects to a WebSocket server and streams real-time patient data.
 * <p>
 * This class listens for incoming messages, parses them into patient data,
 * and stores the processed information in the provided {@link DataStorage} instance.
 * <p>
 * Expected message format: {@code patientId|type|value|timestamp}, for example:
 * {@code 123|heartRate|97.2|1716632543000}.
 */
public class WebSocketClient extends org.java_websocket.client.WebSocketClient {

    private final DataStorage dataStorage;

    /**
     * Constructs a WebSocketClient with the specified server URI and data storage.
     *
     * @param serverUri    the URI of the WebSocket server to connect to
     * @param dataStorage  the storage object where parsed data will be saved
     */
    public WebSocketClient(URI serverUri, DataStorage dataStorage) {
        super(serverUri);
        this.dataStorage = dataStorage;
    }

    /**
     * Called when the WebSocket connection is successfully opened.
     *
     * @param handshakedata the handshake data provided by the server
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to WebSocket server");
    }

    /**
     * Called when a new message is received from the WebSocket server.
     * Parses and stores patient data if the message format is valid.
     *
     * @param message the incoming message string
     */
    @Override
    public void onMessage(String message) {
        try {
            String[] parts = message.split("\\|");
            if (parts.length != 4) {
                System.err.println("Incorrect format: " + message);
                return;
            }

            int patientIdInt = Integer.parseInt(parts[0]);
            String type = parts[1];
            double value = Double.parseDouble(parts[2]);
            long timestamp = Long.parseLong(parts[3]);

            String measurementValue = String.valueOf(value);

            dataStorage.addPatientData(patientIdInt, measurementValue, type, timestamp);

        } catch (NumberFormatException e) {
            System.err.println("Incorrect format in message: " + message);
        } catch (Exception e) {
            System.err.println("Failed processing message: " + message);
            e.printStackTrace();
        }
    }

    /**
     * Called when the WebSocket connection is closed.
     *
     * @param code    the status code indicating why the connection was closed
     * @param reason  the reason for the closure
     * @param remote  whether the closing came from the remote peer
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WebSocket closed: " + reason);
    }

    /**
     * Called when an error occurs on the WebSocket connection.
     *
     * @param ex the exception representing the error
     */
    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
        ex.printStackTrace();
    }
}
