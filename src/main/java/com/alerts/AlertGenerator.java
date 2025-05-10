package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;

/**
 * This class checks patient data and decides whether an alert should be triggered.
 * It uses the DataStorage object to access all the patient records.
 */
public class AlertGenerator {

    /**
     * Used to access patient data for checking alert conditions.
     */
    private final DataStorage dataStorage; // Added final to dataStorage to follow Google Style

    /**
     * Creates a new alert generator that uses the given data storage.
     *
     * @param dataStorage the system that holds patient information
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Goes through the records of one patient and checks if any values
     * should trigger an alert (like something being too high or too low).
     * The actual logic still needs to be implemented.
     *
     * @param patient the patient whose data should be evaluated
     */
    public void evaluateData(Patient patient) {
        // Replaced placeholder with proper comment to follow style guide
        // This method should be implemented based on alert criteria
    }

    /**
     * Triggers an alert based on the data. This method can be used
     * to notify someone or just log the problem. For now, it's just a placeholder.
     *
     * @param alert the alert that should be sent or handled
     */
    private void triggerAlert(Alert alert) {
        // Can be used later to notify medical staff or write to a log
    }
}
