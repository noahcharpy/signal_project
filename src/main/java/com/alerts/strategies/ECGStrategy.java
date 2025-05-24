package com.alerts.strategies;

import com.alerts.factories.AlertFactory;
import com.alerts.factories.AlertFactoryProvider;
import com.alerts.AlertTrigger;
import com.data_management.PatientRecord;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy for detecting abnormal ECG peaks.
 * Triggers an alert if a data point significantly exceeds the average.
 */
public class ECGStrategy implements AlertStrategy {

    /**
     * Checks for abnormal ECG peaks based on the average of recent readings.
     *
     * @param patientId the ID of the patient being evaluated
     * @param records   the list of ECG records to evaluate
     * @param trigger   the trigger used to dispatch alerts
     */
    @Override
    public void checkAlert(String patientId, List<PatientRecord> records, AlertTrigger trigger) {
        // Use the correct factory for ECG alerts
        AlertFactory factory = AlertFactoryProvider.getFactory("ecg");

        List<PatientRecord> ecgRecords = records.stream()
                .filter(r -> r.getRecordType().equalsIgnoreCase("ECG"))
                .collect(Collectors.toList());

        if (ecgRecords.size() < 5) {
            return;
        }

        List<Double> values = ecgRecords.stream()
                .map(r -> {
                    try {
                        return Double.parseDouble(r.getMeasurementValue());
                    } catch (NumberFormatException e) {
                        return 0.0;
                    }
                }).collect(Collectors.toList());

        double avg = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) > avg * 1.3) {
                trigger.trigger(factory.createAlert(
                        patientId,
                        "Abnormal ECG Peak",
                        ecgRecords.get(i).getTimestamp()
                ));
            }
        }
    }
}
