package com.data_management;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PatientTest {

    @Test
    void testAddAndRetrieveRecord() {
        Patient patient = new Patient(1);
        long now = System.currentTimeMillis();

        patient.addRecord("98%", "Saturation", now);
        List<PatientRecord> records = patient.getRecords(now - 1000, now + 1000);

        assertEquals(1, records.size());
        assertEquals("Saturation", records.get(0).getRecordType());
    }

    @Test
    void testGetRecordsNoMatch() {
        Patient patient = new Patient(2);
        long now = System.currentTimeMillis();

        patient.addRecord("120/80", "BloodPressure", now);
        List<PatientRecord> records = patient.getRecords(now + 10000, now + 20000);

        assertTrue(records.isEmpty(), "Expected no records in this time range");
    }
}
