package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class goes through the latest patient data and triggers alerts
 * when specific medical conditions are detected based on value thresholds
 * or abnormal trends.
 */
public class AlertGenerator {

    /** Used to access the patient data for processing. */
    private final DataStorage dataStorage;

    /**
     * Creates an AlertGenerator that checks data from the given storage.
     *
     * @param dataStorage the system containing patient records
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Looks at a patient's records from the last 10 minutes and checks for anything
     * unusual (high blood pressure, low oxygen, etc.).
     *
     * @param patient the patient whose data we want to check
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

    /**
     * Checks if the patient's blood pressure readings meet any critical thresholds
     * or show a rising/falling trend.
     */
    private void checkBloodPressure(String id, List<PatientRecord> records) {
        List<Integer> systolics = new ArrayList<>();
        List<Integer> diastolics = new ArrayList<>();

        for (PatientRecord r : records) {
            String[] parts = r.getMeasurementValue().split("/");
            if (parts.length != 2) continue;

            try {
                int sys = Integer.parseInt(parts[0].trim());
                int dia = Integer.parseInt(parts[1].trim());

                // Critical thresholds
                if (sys > 180 || sys < 90 || dia > 120 || dia < 60) {
                    triggerAlert(new Alert(id, "Critical Blood Pressure", r.getTimestamp()));
                }

                systolics.add(sys);
                diastolics.add(dia);

            } catch (NumberFormatException ignored) {}
        }

        checkTrend(id, systolics, "Systolic BP");
        checkTrend(id, diastolics, "Diastolic BP");
    }

    /**
     * Triggers a trend alert if values rise or fall 3 times in a row by more than 10.
     */
    private void checkTrend(String id, List<Integer> values, String label) {
        for (int i = 0; i <= values.size() - 3; i++) {
            int v1 = values.get(i);
            int v2 = values.get(i + 1);
            int v3 = values.get(i + 2);

            boolean rising = v2 - v1 > 10 && v3 - v2 > 10;
            boolean falling = v1 - v2 > 10 && v2 - v3 > 10;

            if (rising) {
                triggerAlert(new Alert(id, "Rising " + label + " Trend", System.currentTimeMillis()));
            } else if (falling) {
                triggerAlert(new Alert(id, "Falling " + label + " Trend", System.currentTimeMillis()));
            }
        }
    }

    /**
     * Checks for low saturation or sudden drops.
     */
    private void checkSaturation(String id, List<PatientRecord> records) {
        records.sort(Comparator.comparingLong(PatientRecord::getTimestamp));

        for (int i = 0; i < records.size(); i++) {
            double val = parsePercentage(records.get(i).getMeasurementValue());

            if (val < 92) {
                triggerAlert(new Alert(id, "Low Saturation", records.get(i).getTimestamp()));
            }

            for (int j = i + 1; j < records.size(); j++) {
                double next = parsePercentage(records.get(j).getMeasurementValue());
                if (val - next >= 5) {
                    triggerAlert(new Alert(id, "Rapid Saturation Drop", records.get(j).getTimestamp()));
                }
            }
        }
    }

    /**
     * Triggers alert if systolic < 90 and SpO2 < 92 in the same 10-minute window.
     */
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
                        triggerAlert(new Alert(id, "Hypotensive Hypoxemia", timestamp));
                        return;
                    }
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    /**
     * Looks for sudden ECG peaks that are far above the average.
     */
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
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) > avg * 1.3) {
                triggerAlert(new Alert(id, "Abnormal ECG Peak", ecg.get(i).getTimestamp()));
            }
        }
    }

    /**
     * Parses a string like "97%" to a usable number.
     */
    private double parsePercentage(String input) {
        return Double.parseDouble(input.replace("%", "").trim());
    }

    /**
     * Picks out only the records with the given type label.
     */
    private List<PatientRecord> filterByType(List<PatientRecord> all, String type) {
        return all.stream()
                .filter(r -> r.getRecordType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    /**
     * Placeholder for what should happen when an alert is triggered.
     * For now, it just prints to console.
     */
    private void triggerAlert(Alert alert) {
        System.out.println(" ALERT for Patient " + alert.getPatientId()
                + ": " + alert.getCondition() + " at " + alert.getTimestamp());
    }
}
