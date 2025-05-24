package com.alerts;

import com.alerts.factories.AlertFactory;
import com.alerts.strategies.AlertStrategy;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Central service that applies different alert strategies based on patient data type.
 */
public class AlertService {

    private final AlertFactory alertFactory;
    private final Map<String, AlertStrategy> strategies;
    private final AlertTrigger trigger;

    /**
     * Constructs an AlertService with required components.
     *
     * @param alertFactory the factory to create alert objects
     * @param strategies a map connecting each record type to its corresponding alert strategy
     * @param trigger the alert trigger responsible for dispatching alerts
     */
    public AlertService(AlertFactory alertFactory, Map<String, AlertStrategy> strategies, AlertTrigger trigger) {
        this.alertFactory = alertFactory;
        this.strategies = strategies;
        this.trigger = trigger;
    }

    /**
     * Evaluates a patient's recent data and uses the right strategy to check for alerts.
     *
     * @param patient the patient whose data should be evaluated
     */
    public void evaluate(Patient patient) {
        long now = System.currentTimeMillis();
        long tenMinutesAgo = now - 10 * 60 * 1000;
        String id = String.valueOf(patient.getPatientId());

        List<PatientRecord> records = patient.getRecords(tenMinutesAgo, now);
        Map<String, List<PatientRecord>> grouped = records.stream()
                .collect(Collectors.groupingBy(PatientRecord::getRecordType));

        for (Map.Entry<String, List<PatientRecord>> entry : grouped.entrySet()) {
            String type = entry.getKey();
            List<PatientRecord> typeRecords = entry.getValue();

            AlertStrategy strategy = strategies.get(type);
            if (strategy != null) {
                strategy.checkAlert(id, typeRecords, trigger);
            }
        }
    }
}
