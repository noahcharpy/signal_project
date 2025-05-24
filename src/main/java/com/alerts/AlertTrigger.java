package com.alerts;

/**
 * Functional interface for triggering an alert.
 * This allows strategies to send alerts without knowing how they're handled.
 */
@FunctionalInterface
public interface AlertTrigger {

    /**
     * Called when an alert condition is detected.
     *
     * @param alert The alert to be triggered.
     */
    void trigger(Alert alert);
}
