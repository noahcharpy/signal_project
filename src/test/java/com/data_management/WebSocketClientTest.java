package com.data_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the WebSocketClient class by sending example messages
 * and verifying that patient data is stored correctly.
 */
public class WebSocketClientTest {

    private DataStorage storage;
    private WebSocketClient client;

    /**
     * Sets up the test with a clean DataStorage instance before each test.
     *
     * @throws Exception if URI is malformed
     */
    @BeforeEach
    public void setUp() throws Exception {
        storage = DataStorage.getInstance();
        storage.clear();  // reset the singleton's internal map
        client = new WebSocketClient(new URI("ws://localhost:1234"), storage);
    }

    /**
     * Verifies that a valid message results in a new patient record being added.
     */
    @Test
    public void testOnMessage_withValidMessage() {
        String validMessage = "123|heartRate|98.6|1716632543000";

        client.onMessage(validMessage);

        List<PatientRecord> records = storage.getRecords(123, 1716632543000L, 1716632543000L);
        assertEquals(1, records.size(), "Expected one record for patient 123");

        PatientRecord record = records.get(0);
        assertEquals("heartRate", record.getRecordType());
        assertEquals("98.6", record.getMeasurementValue());
        assertEquals(1716632543000L, record.getTimestamp());
    }

    /**
     * Checks that malformed messages do not result in stored data.
     */
    @Test
    public void testOnMessage_withInvalidFormat() {
        String badMessage = "bad|format|only";

        client.onMessage(badMessage);

        assertTrue(storage.getAllPatients().isEmpty(), "Expected no patients to be added for invalid format");
    }

    /**
     * Checks that messages with non-numeric IDs or timestamps are safely ignored.
     */
    @Test
    public void testOnMessage_withInvalidNumbers() {
        String badNumberMessage = "abc|heartRate|notANumber|badTimestamp";

        client.onMessage(badNumberMessage);

        assertTrue(storage.getAllPatients().isEmpty(), "Expected no data to be stored for invalid numeric values");
    }
}
