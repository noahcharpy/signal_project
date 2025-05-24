package com.alerts;

import com.alerts.factories.*;
import com.alerts.strategies.*;
import com.data_management.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that the alert system correctly triggers all strategies and returns the appropriate alert types.
 */
class AlertServiceTest {

    private DataStorage storage;
    private TestAlertTrigger trigger;
    private AlertService alertService;

    @BeforeEach
    void setUp() {
        storage = DataStorage.getInstance();
        trigger = new TestAlertTrigger();

        Map<String, AlertStrategy> strategies = new HashMap<>();
        strategies.put("bloodpressure", new BloodPressureStrategy());
        strategies.put("saturation", new OxygenSaturationStrategy());
        strategies.put("ecg", new ECGStrategy());
        strategies.put("heartrate", new HeartRateStrategy());

        // AlertFactory is now resolved inside each strategy, so we can pass null
        alertService = new AlertService(null, strategies, trigger);
    }

    @Test
    void testAllAlertStrategies() {
        long now = System.currentTimeMillis();
        int patientId = 1;

        // Trigger Low Saturation + Rapid Drop
        storage.addPatientData(patientId, "97%", "saturation", now - 5000);
        storage.addPatientData(patientId, "89%", "saturation", now);

        // Trigger Critical Blood Pressure + Systolic Trend
        storage.addPatientData(patientId, "100/70", "bloodpressure", now - 9000);
        storage.addPatientData(patientId, "115/75", "bloodpressure", now - 6000);
        storage.addPatientData(patientId, "130/80", "bloodpressure", now - 3000);
        storage.addPatientData(patientId, "190/130", "bloodpressure", now);

        // Trigger Abnormal ECG Peak
        storage.addPatientData(patientId, "0.5", "ecg", now - 50000);
        storage.addPatientData(patientId, "0.6", "ecg", now - 40000);
        storage.addPatientData(patientId, "0.5", "ecg", now - 30000);
        storage.addPatientData(patientId, "0.6", "ecg", now - 20000);
        storage.addPatientData(patientId, "1.2", "ecg", now); // Spike

        // Trigger Heart Rate
        storage.addPatientData(patientId, "150", "heartrate", now);
        storage.addPatientData(patientId, "25", "heartrate", now);

        Patient patient = storage.getPatient(patientId);
        alertService.evaluate(patient);

        // Ensure alerts were triggered
        assertFalse(trigger.alerts.isEmpty(), "Alerts should have been triggered");

        // Collect all condition descriptions
        List<String> conditions = trigger.alerts.stream()
                .map(Alert::getCondition)
                .collect(Collectors.toList());

        // Check expected alert conditions
        assertTrue(conditions.contains("Low Saturation"));
        assertTrue(conditions.contains("Rapid Saturation Drop"));
        assertTrue(conditions.contains("Critical Blood Pressure"));
        assertTrue(conditions.contains("Rising Systolic BP Trend"));
        assertTrue(conditions.contains("Abnormal ECG Peak"));
        assertTrue(conditions.contains("Low Heart Rate"));
        assertTrue(conditions.contains("High Heart Rate"));

        // Additional polymorphism and subclass checks
        for (Alert alert : trigger.alerts) {
            assertNotEquals("Generic", alert.getAlertType(), "Alert type should not be 'Generic'");
            assertTrue(alert.getPriority() >= 0, "Priority should be non-negative");

            switch (alert.getAlertType().toLowerCase()) {
                case "heartrate":
                    assertTrue(alert instanceof HeartRateAlert, "Expected HeartRateAlert subclass");
                    break;
                case "ecg":
                    assertTrue(alert instanceof ECGAlert, "Expected ECGAlert subclass");
                    break;
                case "bloodpressure":
                    assertTrue(alert instanceof BloodPressureAlert, "Expected BloodPressureAlert subclass");
                    break;
                case "saturation":
                    assertTrue(alert instanceof SaturationAlert, "Expected SaturationAlert subclass");
                    break;
                default:
                    fail("Unexpected alert type: " + alert.getAlertType());
            }
        }
    }

    /**
     * A simple test implementation of AlertTrigger to store triggered alerts.
     */
    static class TestAlertTrigger implements AlertTrigger {
        List<Alert> alerts = new ArrayList<>();

        @Override
        public void trigger(Alert alert) {
            alerts.add(alert);
        }
    }
}
