package com.alerts;

/**
 * An alert related to irregular ECG signals.
 */
public class ECGAlert extends Alert {

    /**
     * Constructs a new ECG alert.
     *
     * @param patientId the ID of the patient
     * @param condition the alert condition description
     * @param timestamp the time the alert occurred
     */
    public ECGAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }

    @Override
    public String getAlertType() {
        return "ecg";
    }

    @Override
    public int getPriority() {
        return 3; // High urgency
    }
}
