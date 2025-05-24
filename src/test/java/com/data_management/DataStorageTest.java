package com.data_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the DataStorage singleton, ensuring correct data storage and retrieval.
 */
class DataStorageTest {

    @BeforeEach
    void resetStorage() {
        DataStorage.getInstance().clear(); // Ensures test isolation
    }

    @Test
    void testSingletonBehavior() {
        DataStorage first = DataStorage.getInstance();
        DataStorage second = DataStorage.getInstance();
        assertSame(first, second, "Both instances should be the same (singleton)");
    }

    @Test
    void testAddAndGetRecords() {
        DataStorage storage = DataStorage.getInstance();
        long t1 = 1714376789050L;
        long t2 = 1714376789051L;

        storage.addPatientData(1, "100.0", "WhiteBloodCells", t1);
        storage.addPatientData(1, "200.0", "WhiteBloodCells", t2);

        List<PatientRecord> records = storage.getRecords(1, t1, t2);
        assertEquals(2, records.size(), "Both records should be retrieved");
        assertEquals("100.0", records.get(0).getMeasurementValue(), "First record should match input");
    }

    @Test
    void testNoRecordsOutsideTimeRange() {
        DataStorage storage = DataStorage.getInstance();
        long now = System.currentTimeMillis();

        storage.addPatientData(1, "75.0", "ECG", now);
        List<PatientRecord> results = storage.getRecords(1, now + 1000, now + 5000);

        assertTrue(results.isEmpty(), "No records should be returned for future range");
    }

    @Test
    void testGetAllPatientsReturnsUniquePatients() {
        DataStorage storage = DataStorage.getInstance();

        storage.addPatientData(1, "90.0", "Saturation", System.currentTimeMillis());
        storage.addPatientData(2, "100.0", "ECG", System.currentTimeMillis());
        storage.addPatientData(1, "92.0", "Saturation", System.currentTimeMillis());

        List<Patient> patients = storage.getAllPatients();
        assertEquals(2, patients.size(), "Should contain 2 unique patients");
    }

    @Test
    void testClearResetsStorage() {
        DataStorage storage = DataStorage.getInstance();
        storage.addPatientData(1, "123", "Test", System.currentTimeMillis());
        storage.clear();
        assertTrue(storage.getAllPatients().isEmpty(), "Storage should be empty after clear");
    }
}
