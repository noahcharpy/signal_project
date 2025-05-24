package com.alerts;

/**
 * Base class representing a general patient alert.
 */
public class Alert {
    private final String patientId;
    private final String condition;
    private final long timestamp;

    /**
     * Constructs a new generic alert.
     *
     * @param patientId the ID of the patient
     * @param condition the description of the alert condition
     * @param timestamp the time the alert was triggered
     */
    public Alert(String patientId, String condition, long timestamp) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getCondition() {
        return condition;
    }

    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the type of the alert (can be overridden).
     *
     * @return a string representing the alert type
     */
    public String getAlertType() {
        return "Generic";
    }

    /**
     * Returns the priority of the alert (0 = normal).
     * Can be overridden or decorated.
     *
     * @return an integer priority level
     */
    public int getPriority() {
        return 0;
    }

    @Override
    public String toString() {
        return String.format("[%s Alert] %s - Patient: %s (Priority %d)",
                getAlertType(), condition, patientId, getPriority());
    }
}
