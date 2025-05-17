package com.alerts;

import com.data_management.*;
import org.junit.jupiter.api.Test;

class AlertGeneratorTest {

    @Test
    void testLowSaturationAlert() {
        DataStorage storage = new DataStorage();
        long now = System.currentTimeMillis();

        storage.addPatientData(1, "89.0", "Saturation", now); // Low
        AlertGenerator ag = new AlertGenerator(storage);

        Patient patient = storage.getAllPatients().get(0);
        ag.evaluateData(patient); // Should print alert
    }

    @Test
    void testBloodPressureTrendAlert() {
        DataStorage storage = new DataStorage();
        long now = System.currentTimeMillis();

        // Systolic values increasing: 100 → 115 → 130
        storage.addPatientData(1, "100/70", "BloodPressure", now - 8000);
        storage.addPatientData(1, "115/75", "BloodPressure", now - 6000);
        storage.addPatientData(1, "130/80", "BloodPressure", now - 4000);

        AlertGenerator ag = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        ag.evaluateData(patient);
    }

    @Test
    void testHypotensiveHypoxemia() {
        DataStorage storage = new DataStorage();
        long now = System.currentTimeMillis();

        storage.addPatientData(1, "85/60", "BloodPressure", now);
        storage.addPatientData(1, "89.0", "Saturation", now);

        AlertGenerator ag = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        ag.evaluateData(patient);
    }

    @Test
    void testAbnormalECGPeak() {
        DataStorage storage = new DataStorage();
        long now = System.currentTimeMillis();

        // 5 normal, 1 peak
        storage.addPatientData(1, "70.0", "ECG", now - 60000);
        storage.addPatientData(1, "71.0", "ECG", now - 50000);
        storage.addPatientData(1, "69.0", "ECG", now - 40000);
        storage.addPatientData(1, "72.0", "ECG", now - 30000);
        storage.addPatientData(1, "70.5", "ECG", now - 20000);
        storage.addPatientData(1, "95.0", "ECG", now); // Big peak

        AlertGenerator ag = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        ag.evaluateData(patient);
    }
}
