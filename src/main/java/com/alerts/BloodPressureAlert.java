package com.alerts;

/**
 * An alert related to abnormal blood pressure values.
 */
public class BloodPressureAlert extends Alert {

    /**
     * Constructs a new blood pressure alert.
     *
     * @param patientId the ID of the patient
     * @param condition the alert condition description
     * @param timestamp the time the alert occurred
     */
    public BloodPressureAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }

    @Override
    public String getAlertType() {
        return "bloodpressure";
    }

    @Override
    public int getPriority() {
        return 2; // Moderate urgency
    }
}
