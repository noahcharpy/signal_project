package com.data_management;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WebSocketDataReaderTest {

    private TestWebSocketServer mockServer;
    private WebSocketDataReader reader;
    private DataStorage storage;

    @BeforeEach
    public void setUp() throws Exception {
        // Start a lightweight WebSocket server on port 1234
        mockServer = new TestWebSocketServer(1234);
        mockServer.start();

        storage = new DataStorage();
        reader = new WebSocketDataReader();

        // Actively wait for the server to be ready
        Thread.sleep(500);  // give server time to bind
    }

    @AfterEach
    public void tearDown() throws Exception {
        reader.stopStreaming();
        mockServer.stop();
    }

    @Test
    public void testStartStreaming_initializesAndConnects() {
        assertDoesNotThrow(() -> reader.startStreaming(storage));
    }

    @Test
    public void testStopStreaming_closesConnectionGracefully() {
        reader.startStreaming(storage);
        assertDoesNotThrow(reader::stopStreaming);
    }

    @Test
    public void testDataFlow_endToEndFromServerToClient() throws Exception {
        reader.startStreaming(storage);

        // Wait for client to connect
        long start = System.currentTimeMillis();
        while (mockServer.getConnections().isEmpty()) {
            if (System.currentTimeMillis() - start > 3000) {
                fail("Timeout: WebSocket client did not connect to server.");
            }
            Thread.sleep(50);
        }

        // Send a well-formatted message to the client
        String msg = "100|heartRate|98.5|1716632543000";
        mockServer.broadcast(msg);

        Thread.sleep(500);  // Give time to process message

        Patient patient = storage.getPatient(100);
        assertNotNull(patient, "Expected patient to be created");
        List<PatientRecord> records = patient.getRecords(0, System.currentTimeMillis());
        assertEquals(1, records.size(), "Expected exactly one patient record");
        assertEquals("heartRate", records.get(0).getRecordType());
        assertEquals("98.5", records.get(0).getMeasurementValue());
    }

    @Test
    public void testHandlesInvalidMessageFormat() throws Exception {
        reader.startStreaming(storage);

        // Wait for client to connect
        long start = System.currentTimeMillis();
        while (mockServer.getConnections().isEmpty()) {
            if (System.currentTimeMillis() - start > 3000) {
                fail("Timeout: WebSocket client did not connect to server.");
            }
            Thread.sleep(50);
        }

        mockServer.broadcast("bad|message|format");
        Thread.sleep(300);  // Give client time to reject message

        // Assert no patient was added
        assertTrue(storage.getAllPatients().isEmpty(), "Storage should remain empty on malformed input");
    }

    /**
     * Lightweight WebSocket server for integration testing.
     */
    private static class TestWebSocketServer extends WebSocketServer {
        public TestWebSocketServer(int port) {
            super(new InetSocketAddress(port));
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            System.out.println("Mock server: client connected");
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            System.out.println("Mock server: client disconnected");
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            // Not needed for test
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            System.err.println("Mock server error: " + ex.getMessage());
        }

        @Override
        public void onStart() {
            System.out.println("Mock server started on port " + getPort());
        }
    }
}
