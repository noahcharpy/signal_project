package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * WebSocketClientDataReader connects to a WebSocket server,
 * listens for patient data messages, parses them, and stores them.
 */
public class WebSocketClientDataReader extends WebSocketClient implements DataReader {

    private final DataStorage storage;

    public WebSocketClientDataReader(String serverUri, DataStorage storage) throws URISyntaxException {
        super(new URI(serverUri));
        this.storage = storage;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to WebSocket server.");
    }

    @Override
    public void onMessage(String message) {
        try {
            String[] parts = message.split(",");
            int patientId = Integer.parseInt(parts[0]);
            long timestamp = Long.parseLong(parts[1]);
            String recordType = parts[2];
            String data = parts[3];

            storage.addPatientData(patientId, data, recordType, timestamp);
            System.out.println("Received and stored data: " + message);
        } catch (Exception e) {
            System.err.println("Failed to parse message: " + message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from server: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
    }

    @Override
    public void readData(DataStorage dataStorage) {
        this.connect();
    }
}
