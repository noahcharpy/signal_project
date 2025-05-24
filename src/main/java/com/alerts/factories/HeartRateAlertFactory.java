package com.alerts.factories;

import com.alerts.Alert;
import com.alerts.factories.AlertFactory;
import com.alerts.HeartRateAlert;

/**
 * Factory class for creating heart rate related alerts.
 */
public class HeartRateAlertFactory implements AlertFactory {

    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new HeartRateAlert(patientId, condition, timestamp);
    }
}
