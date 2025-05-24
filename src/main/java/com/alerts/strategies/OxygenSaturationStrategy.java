package com.alerts.strategies;

import com.alerts.factories.AlertFactory;
import com.alerts.factories.AlertFactoryProvider;
import com.alerts.AlertTrigger;
import com.data_management.PatientRecord;

import java.util.Comparator;
import java.util.List;

/**
 * Strategy for detecting oxygen saturation issues in patient records.
 * Checks for low saturation and rapid drops between consecutive readings.
 */
public class OxygenSaturationStrategy implements AlertStrategy {

    /**
     * Evaluates saturation data and triggers alerts if necessary.
     *
     * @param patientId the ID of the patient
     * @param records   the list of saturation records
     * @param trigger   the alert trigger used to dispatch alerts
     */
    @Override
    public void checkAlert(String patientId, List<PatientRecord> records, AlertTrigger trigger) {
        AlertFactory factory = AlertFactoryProvider.getFactory("saturation");

        records.sort(Comparator.comparingLong(PatientRecord::getTimestamp));

        for (int i = 0; i < records.size(); i++) {
            double val = parsePercentage(records.get(i).getMeasurementValue());

            if (val < 92) {
                trigger.trigger(factory.createAlert(patientId, "Low Saturation", records.get(i).getTimestamp()));
            }

            for (int j = i + 1; j < records.size(); j++) {
                double next = parsePercentage(records.get(j).getMeasurementValue());
                if (val - next >= 5) {
                    trigger.trigger(factory.createAlert(patientId, "Rapid Saturation Drop", records.get(j).getTimestamp()));
                }
            }
        }
    }

    /**
     * Parses a string like "97%" into a numeric value.
     *
     * @param input the saturation string
     * @return the numeric value
     */
    private double parsePercentage(String input) {
        return Double.parseDouble(input.replace("%", "").trim());
    }
}
