package com.data_management;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataStorageTest {

    @Test
    void testAddAndGetRecords() {
        DataStorage storage = new DataStorage();
        long t1 = 1714376789050L;
        long t2 = 1714376789051L;

        // Add two measurements for the same patient and type
        storage.addPatientData(1, "100.0", "WhiteBloodCells", t1);
        storage.addPatientData(1, "200.0", "WhiteBloodCells", t2);

        List<PatientRecord> records = storage.getRecords(1, t1, t2);

        assertEquals(2, records.size()); // both records should be included
        assertEquals(100.0, Double.parseDouble(records.get(0).getMeasurementValue())); // check first value
    }

    @Test
    void testNoRecordsOutsideTimeRange() {
        DataStorage storage = new DataStorage();
        long now = System.currentTimeMillis();

        // Data outside requested range
        storage.addPatientData(1, "75.0", "ECG", now);

        List<PatientRecord> results = storage.getRecords(1, now + 1000, now + 5000);
        assertTrue(results.isEmpty()); // should return no records
    }

    @Test
    void testGetAllPatientsReturnsUniquePatients() {
        DataStorage storage = new DataStorage();

        storage.addPatientData(1, "90.0", "Saturation", System.currentTimeMillis());
        storage.addPatientData(2, "100.0", "ECG", System.currentTimeMillis());
        storage.addPatientData(1, "92.0", "Saturation", System.currentTimeMillis()); // duplicate ID

        assertEquals(2, storage.getAllPatients().size()); // only 2 unique patients
    }
}
