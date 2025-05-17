package com.data_management;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PatientRecordTest {

    @Test
    void testRecordFields() {
        long timestamp = System.currentTimeMillis();
        PatientRecord record = new PatientRecord(1, "98%", "Saturation", timestamp);

        assertEquals(1, record.getPatientId());
        assertEquals("98%", record.getMeasurementValue());
        assertEquals("Saturation", record.getRecordType());
        assertEquals(timestamp, record.getTimestamp());
    }
}
