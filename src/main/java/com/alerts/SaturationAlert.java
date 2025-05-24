package com.alerts;

/**
 * An alert related to low blood oxygen saturation.
 */
public class SaturationAlert extends Alert {

    /**
     * Constructs a new saturation alert.
     *
     * @param patientId the ID of the patient
     * @param condition the alert condition description
     * @param timestamp the time the alert occurred
     */
    public SaturationAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }

    @Override
    public String getAlertType() {
        return "saturation";
    }

    @Override
    public int getPriority() {
        return 2; // Moderate urgency
    }
}
