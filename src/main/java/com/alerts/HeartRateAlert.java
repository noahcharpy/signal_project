package com.alerts;

/**
 * An alert related to abnormal heart rate readings.
 */
public class HeartRateAlert extends Alert {

    /**
     * Constructs a new heart rate alert.
     *
     * @param patientId the ID of the patient
     * @param condition the alert condition description
     * @param timestamp the time the alert occurred
     */
    public HeartRateAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }

    @Override
    public String getAlertType() {
        return "heartrate";
    }

    @Override
    public int getPriority() {
        return 1; // Lower urgency
    }
}
