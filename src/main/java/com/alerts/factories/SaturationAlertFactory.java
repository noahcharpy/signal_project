package com.alerts.factories;

import com.alerts.Alert;
import com.alerts.factories.AlertFactory;
import com.alerts.SaturationAlert;

/**
 * Factory class for creating blood oxygen saturation alerts.
 */
public class SaturationAlertFactory implements AlertFactory {

    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new SaturationAlert(patientId, condition, timestamp);
    }
}
