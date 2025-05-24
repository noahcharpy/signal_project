package com.alerts.factories;

import com.alerts.Alert;

/**
 * A factory interface for creating Alert objects based on patient data.
 */
public interface AlertFactory {

    /**
     * Creates an Alert instance using the provided details.
     *
     * @param patientId the ID of the patient the alert is related to
     * @param condition a description of the condition that triggered the alert
     * @param timestamp the time when the alert was triggered
     * @return a new Alert object
     */
    Alert createAlert(String patientId, String condition, long timestamp);
}
