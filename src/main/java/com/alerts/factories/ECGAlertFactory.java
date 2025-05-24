package com.alerts.factories;

import com.alerts.Alert;
import com.alerts.factories.AlertFactory;
import com.alerts.ECGAlert;

/**
 * Factory class for creating ECG related alerts.
 */
public class ECGAlertFactory implements AlertFactory {

    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new ECGAlert(patientId, condition, timestamp);
    }
}
