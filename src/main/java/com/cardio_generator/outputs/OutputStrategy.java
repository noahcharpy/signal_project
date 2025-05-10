package com.cardio_generator.outputs;

/**
 * An interface for sending patient data to an output.
 */
public interface OutputStrategy {

    /**
     * Sends one line of patient data to the chosen output.
     *
     * @param patientId the ID of the patient
     * @param timestamp the time the data was generated (in milliseconds)
     * @param label     the type of signal (e.g., "ECG", "HeartRate")
     * @param data      the actual value to be written or transmitted
     */
    void output(int patientId, long timestamp, String label, String data);
}
