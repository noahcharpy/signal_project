package com.data_management;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class WebSocketIntegrationTest {

    private TestWebSocketServer mockServer;
    private WebSocketDataReader reader;
    private DataStorage storage;

    @BeforeEach
    public void setUp() throws Exception {
        //Start a mock server
        mockServer = new TestWebSocketServer(1234);
        mockServer.start();
        mockServer.awaitStart();

        storage = DataStorage.getInstance();
        storage.clear();

        reader = new WebSocketDataReader();
        reader.startStreaming(storage);

        // Actively wait until the client is connected
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 5000) {
            if (reader.isClientOpen()) {  //
                return; // connection successful
            }
            Thread.sleep(50);
        }

        fail("Timeout: WebSocket client did not connect to the server.");
    }

    @AfterEach
    public void tearDown() throws Exception {
        reader.stopStreaming();
        mockServer.stop();
        storage.clear();
    }

    @Test
    public void testEndToEndMessageFlow() throws Exception {
        String testMessage = "123|heartRate|97.2|1716632543000";

        //Send message from server to client
        mockServer.broadcast(testMessage);

        //Wait for message to process
        Thread.sleep(500);

        //Verify if data arrived in DataStorage
        Patient patient = storage.getPatient(123);
        assertNotNull(patient);

        List<PatientRecord> records = patient.getRecords(0, System.currentTimeMillis());
        assertFalse(records.isEmpty());

        PatientRecord record = records.get(0);
        assertEquals("97.2", record.getMeasurementValue());
        assertEquals("heartRate", record.getRecordType());
    }


    /**
     * Lightweight mock WebSocket server.
     */
    private static class TestWebSocketServer extends WebSocketServer {
        private final CountDownLatch serverStartedLatch = new CountDownLatch(1);

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
            // No client messages expected in this test
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            System.err.println("Mock server error: " + ex.getMessage());
        }

        @Override
        public void onStart() {
            System.out.println("Mock server started on port " + getPort());
            serverStartedLatch.countDown(); // Signal that the server is ready
        }

        /**
         * Blocks until the server has fully started.
         */
        public void awaitStart() throws InterruptedException {
            if (!serverStartedLatch.await(5, TimeUnit.SECONDS)) {
                throw new RuntimeException("Mock server failed to start in time.");
            }
        }
    }
}

