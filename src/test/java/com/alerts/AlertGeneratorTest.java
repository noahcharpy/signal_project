package com.alerts;
import com.alerts.factories.*;
import com.alerts.strategies.*;
import com.data_management.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AlertGenerator logic using real factories and subclass alerts.
 */
class AlertGeneratorTest {

    private DataStorage storage;
    private TestAlertTrigger trigger;
    private AlertEvaluator generator;
    private long now;

    @BeforeEach
    void setUp() {
        storage = DataStorage.getInstance();
        storage.clear();
        trigger = new TestAlertTrigger();
        generator = new AlertEvaluator(storage, trigger);
        now = System.currentTimeMillis();
    }

    @Test
    void testLowSaturationAlert() {
        storage.addPatientData(1, "89%", "Saturation", now);
        evaluate();
        assertTriggered("Low Saturation", SaturationAlert.class);
    }

    @Test
    void testRapidSaturationDropAlert() {
        storage.addPatientData(1, "98%", "Saturation", now - 300000);
        storage.addPatientData(1, "92%", "Saturation", now-100000);
        evaluate();

        assertTriggered("Rapid Saturation Drop", SaturationAlert.class);
    }

    @Test
    void testBloodPressureTrendAlert() {
        storage.addPatientData(1, "100/70", "BloodPressure", now - 9000);
        storage.addPatientData(1, "115/70", "BloodPressure", now - 6000);
        storage.addPatientData(1, "130/70", "BloodPressure", now - 3000);
        evaluate();

        assertTriggered("Rising Systolic BP Trend", BloodPressureAlert.class);
    }

    @Test
    void testCriticalBloodPressureAlert() {
        storage.addPatientData(1, "185/130", "BloodPressure", now);
        evaluate();

        assertTriggered("Critical Blood Pressure", BloodPressureAlert.class);
    }

    @Test
    void testHypotensiveHypoxemia() {
        storage.addPatientData(1, "85/60", "BloodPressure", now);
        storage.addPatientData(1, "89%", "Saturation", now);
        evaluate();

        assertTriggered("Hypotensive Hypoxemia", BloodPressureAlert.class);
    }

    @Test
    void testAbnormalECGPeak() {
        storage.addPatientData(1, "0.5", "ECG", now - 50000);
        storage.addPatientData(1, "0.6", "ECG", now - 40000);
        storage.addPatientData(1, "0.5", "ECG", now - 30000);
        storage.addPatientData(1, "0.6", "ECG", now - 20000);
        storage.addPatientData(1, "1.2", "ECG", now);
        evaluate();

        assertTriggered("Abnormal ECG Peak", ECGAlert.class);
    }

    @Test
    void testInvalidBloodPressureFormat() {
        storage.addPatientData(1, "120", "BloodPressure", now); // Invalid
        evaluate();

        // Should trigger nothing
        assertTrue(trigger.alerts.isEmpty(), "No alert should be triggered for malformed input");
    }

    /**
     * Helper method to evaluate current patient.
     */
    private void evaluate() {
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);
    }

    /**
     * Helper method to check that a specific alert was triggered with the expected subclass.
     */
    private void assertTriggered(String expectedCondition, Class<? extends Alert> expectedClass) {

        boolean match = false;

        for (Alert alert : trigger.alerts) {

            if (expectedCondition.equals(alert.getCondition())
                    && expectedClass.isAssignableFrom(alert.getClass()) // use isAssignableFrom instead of isInstance
                    && alert.getPriority() >= 0
                    && !alert.getAlertType().equalsIgnoreCase("generic")) {
                match = true;
            }
        }

        assertTrue(match, "Expected alert not triggered: " + expectedCondition + " [" + expectedClass.getSimpleName() + "]");
    }



    /**
     * Simple alert trigger used to collect alerts for assertions.
     */
    static class TestAlertTrigger implements AlertTrigger {
        List<Alert> alerts = new java.util.ArrayList<>();

        @Override
        public void trigger(Alert alert) {
            alerts.add(alert);
        }
    }
}
