package com.alerts.strategies;

import com.alerts.factories.AlertFactory;
import com.alerts.factories.AlertFactoryProvider;
import com.alerts.AlertTrigger;
import com.data_management.PatientRecord;

import java.util.List;

/**
 * Strategy for detecting abnormal heart rate values.
 * Triggers alerts if heart rate is too low or too high.
 */
public class HeartRateStrategy implements AlertStrategy {

    /**
     * Evaluates heart rate records for a patient and triggers alerts
     * for abnormal values.
     *
     * @param patientId the patient's ID
     * @param records   a list of records of type "HeartRate"
     * @param trigger   the alert trigger responsible for dispatching alerts
     */
    @Override
    public void checkAlert(String patientId, List<PatientRecord> records, AlertTrigger trigger) {
        AlertFactory factory = AlertFactoryProvider.getFactory("heartrate");

        for (PatientRecord record : records) {
            if (!record.getRecordType().equalsIgnoreCase("HeartRate")) continue;

            try {
                double bpm = Double.parseDouble(record.getMeasurementValue());

                if (bpm < 50) {
                    trigger.trigger(factory.createAlert(patientId, "Low Heart Rate", record.getTimestamp()));
                } else if (bpm > 120) {
                    trigger.trigger(factory.createAlert(patientId, "High Heart Rate", record.getTimestamp()));
                }
            } catch (NumberFormatException ignored) {
                // Skip invalid values
            }
        }
    }
}
