package com.alerts.factories;

/**
 * Provides the appropriate AlertFactory implementation based on condition type.
 */
public class AlertFactoryProvider {

    /**
     * Returns an appropriate AlertFactory based on the condition type.
     *
     * @param type the type of condition (e.g. "bloodpressure", "saturation", "ecg", "heartrate")
     * @return an instance of AlertFactory corresponding to the type
     */
    public static AlertFactory getFactory(String type) {
        String key = type.trim().toLowerCase(); // Normalize

        switch (key) {
            case "bloodpressure":
                System.out.println("DEBUG: Using BloodPressureAlertFactory");
                return new BloodPressureAlertFactory();
            case "saturation":
                System.out.println("DEBUG: Using SaturationAlertFactory");
                return new SaturationAlertFactory();
            case "ecg":
                System.out.println("DEBUG: Using ECGAlertFactory");
                return new ECGAlertFactory();
            case "heartrate":
                System.out.println("DEBUG: Using HeartRateAlertFactory");
                return new HeartRateAlertFactory();
            default:
                throw new IllegalArgumentException("Unknown alert type: '" + type + "'");
        }
    }
}
