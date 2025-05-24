package com.alerts;

import com.alerts.factories.AlertFactory;
import com.alerts.factories.AlertFactoryProvider;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AlertEvaluator is responsible for analyzing patient data and triggering alerts
 * when specific medical conditions are detected (e.g. low saturation, abnormal ECG).
 * It uses an AlertFactory to create alerts, allowing different alert types to be generated
 * in a flexible and modular way.
 */
public class AlertEvaluator {

    /** Data source that holds all patient records. */
    private final DataStorage storage;

    /** Trigger used to dispatch Alert objects. */
    private final AlertTrigger trigger;

    /**
     * Creates an AlertEvaluator with a specific data storage and alert trigger.
     *
     * @param storage the source of patient records
     * @param trigger the object used to handle triggered alerts
     */
    public AlertEvaluator(DataStorage storage, AlertTrigger trigger) {
        this.storage = storage;
        this.trigger = trigger;
    }

    /**
     * Looks at the patient's recent data (last 10 minutes) and triggers
     * alerts if something unusual is detected.
     *
     * @param patient the patient whose data should be checked
     */
    public void evaluateData(Patient patient) {
        long now = System.currentTimeMillis();
        long tenMinutesAgo = now - 10 * 60 * 1000;

        List<PatientRecord> recent = patient.getRecords(tenMinutesAgo, now);

        List<PatientRecord> bp = filterByType(recent, "BloodPressure");
        List<PatientRecord> spo2 = filterByType(recent, "Saturation");
        List<PatientRecord> ecg = filterByType(recent, "ECG");

        String id = String.valueOf(patient.getPatientId());

        checkBloodPressure(id, bp);
        checkSaturation(id, spo2);
        checkCombinedHypotensionHypoxemia(id, bp, spo2);
        checkECG(id, ecg);
    }

    private void checkBloodPressure(String id, List<PatientRecord> records) {
        List<Integer> systolics = new ArrayList<>();
        List<Integer> diastolics = new ArrayList<>();

        for (PatientRecord r : records) {
            String[] parts = r.getMeasurementValue().split("/");
            if (parts.length != 2) continue;

            try {
                int sys = Integer.parseInt(parts[0].trim());
                int dia = Integer.parseInt(parts[1].trim());

                if (sys > 180 || sys < 90 || dia > 120 || dia < 60) {
                    AlertFactory factory = AlertFactoryProvider.getFactory("bloodpressure");
                    triggerAlert(factory.createAlert(id, "Critical Blood Pressure", r.getTimestamp()));
                }

                systolics.add(sys);
                diastolics.add(dia);

            } catch (NumberFormatException ignored) {}
        }

        checkTrend(id, systolics, "Systolic BP");
        checkTrend(id, diastolics, "Diastolic BP");
    }

    private void checkTrend(String id, List<Integer> values, String label) {
        for (int i = 0; i <= values.size() - 3; i++) {
            int v1 = values.get(i);
            int v2 = values.get(i + 1);
            int v3 = values.get(i + 2);

            boolean rising = v2 - v1 > 10 && v3 - v2 > 10;
            boolean falling = v1 - v2 > 10 && v2 - v3 > 10;

            if (rising || falling) {
                String condition = rising ? "Rising " : "Falling ";
                AlertFactory factory = AlertFactoryProvider.getFactory("bloodpressure");
                triggerAlert(factory.createAlert(id, condition + label + " Trend", System.currentTimeMillis()));
            }
        }
    }

    private void checkSaturation(String id, List<PatientRecord> records) {
        records.sort(Comparator.comparingLong(PatientRecord::getTimestamp));
        AlertFactory factory = AlertFactoryProvider.getFactory("saturation");

        for (int i = 0; i < records.size(); i++) {
            double val = parsePercentage(records.get(i).getMeasurementValue());

            if (val < 92) {
                triggerAlert(factory.createAlert(id, "Low Saturation", records.get(i).getTimestamp()));
            }

            for (int j = i + 1; j < records.size(); j++) {
                double next = parsePercentage(records.get(j).getMeasurementValue());
                if (val - next >= 5) {
                    triggerAlert(factory.createAlert(id, "Rapid Saturation Drop", records.get(j).getTimestamp()));
                }
            }
        }
    }

    private void checkCombinedHypotensionHypoxemia(String id, List<PatientRecord> bp, List<PatientRecord> spo2) {
        for (PatientRecord b : bp) {
            String[] vals = b.getMeasurementValue().split("/");
            if (vals.length < 1) continue;

            try {
                int sys = Integer.parseInt(vals[0].trim());
                if (sys >= 90) continue;

                for (PatientRecord s : spo2) {
                    double sat = parsePercentage(s.getMeasurementValue());
                    if (sat < 92) {
                        long timestamp = Math.max(b.getTimestamp(), s.getTimestamp());
                        AlertFactory factory = AlertFactoryProvider.getFactory("bloodpressure");
                        triggerAlert(factory.createAlert(id, "Hypotensive Hypoxemia", timestamp));
                        return;
                    }
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    private void checkECG(String id, List<PatientRecord> ecg) {
        if (ecg.size() < 5) return;

        List<Double> values = ecg.stream()
                .map(r -> {
                    try {
                        return Double.parseDouble(r.getMeasurementValue());
                    } catch (Exception e) {
                        return 0.0;
                    }
                }).collect(Collectors.toList());

        double avg = values.stream().mapToDouble(d -> d).average().orElse(0.0);
        AlertFactory factory = AlertFactoryProvider.getFactory("ecg");

        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) > avg * 1.3) {
                triggerAlert(factory.createAlert(id, "Abnormal ECG Peak", ecg.get(i).getTimestamp()));
            }
        }
    }

    private double parsePercentage(String input) {
        return Double.parseDouble(input.replace("%", "").trim());
    }

    private List<PatientRecord> filterByType(List<PatientRecord> all, String type) {
        return all.stream()
                .filter(r -> r.getRecordType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    /**
     * Properly triggers the alert using the registered AlertTrigger.
     */
    private void triggerAlert(Alert alert) {
        System.out.println(" ALERT for Patient " + alert.getPatientId()
                + ": " + alert.getCondition() + " at " + alert.getTimestamp());

        trigger.trigger(alert);
    }
}
