package com.alerts.strategies;

import com.alerts.factories.AlertFactory;
import com.alerts.factories.AlertFactoryProvider;
import com.alerts.AlertTrigger;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy for detecting blood pressure-related alerts.
 * This includes checking for critical thresholds and trends
 * in systolic and diastolic blood pressure values.
 */
public class BloodPressureStrategy implements AlertStrategy {

    /**
     * Checks patient blood pressure records and dispatches alerts
     * for critical values or trends.
     *
     * @param patientId the ID of the patient being evaluated
     * @param records   the recent blood pressure records
     * @param trigger   the alert trigger used to dispatch alerts
     */
    @Override
    public void checkAlert(String patientId, List<PatientRecord> records, AlertTrigger trigger) {
        // Factory is retrieved inside the strategy to match the alert type
        AlertFactory factory = AlertFactoryProvider.getFactory("bloodpressure");

        List<Integer> systolics = new ArrayList<>();
        List<Integer> diastolics = new ArrayList<>();

        for (PatientRecord r : records) {
            if (!r.getRecordType().equalsIgnoreCase("BloodPressure")) continue;

            String[] parts = r.getMeasurementValue().split("/");
            if (parts.length != 2) continue;

            try {
                int sys = Integer.parseInt(parts[0].trim());
                int dia = Integer.parseInt(parts[1].trim());

                // Critical threshold check
                if (sys > 180 || sys < 90 || dia > 120 || dia < 60) {
                    trigger.trigger(factory.createAlert(patientId, "Critical Blood Pressure", r.getTimestamp()));
                }

                systolics.add(sys);
                diastolics.add(dia);
            } catch (NumberFormatException ignored) {
                // Skip malformed entries
            }
        }

        // Trend analysis
        checkTrend(patientId, systolics, "Systolic BP", factory, trigger);
        checkTrend(patientId, diastolics, "Diastolic BP", factory, trigger);
    }

    /**
     * Detects rising or falling trends in blood pressure and triggers alerts.
     *
     * @param patientId the patient ID
     * @param values    the series of values to check
     * @param label     the label used in the alert message
     * @param factory   the alert factory to use
     * @param trigger   the alert trigger to dispatch the alert
     */
    private void checkTrend(String patientId, List<Integer> values, String label,
                            AlertFactory factory, AlertTrigger trigger) {
        for (int i = 0; i <= values.size() - 3; i++) {
            int v1 = values.get(i);
            int v2 = values.get(i + 1);
            int v3 = values.get(i + 2);

            boolean rising = (v2 - v1 > 10) && (v3 - v2 > 10);
            boolean falling = (v1 - v2 > 10) && (v2 - v3 > 10);

            if (rising || falling) {
                String trend = rising ? "Rising " : "Falling ";
                trigger.trigger(factory.createAlert(patientId, trend + label + " Trend", System.currentTimeMillis()));
            }
        }
    }
}
