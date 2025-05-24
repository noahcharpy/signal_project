package com.alerts.decorators;

import com.alerts.Alert;

/**
 * Decorator that increases the priority of an alert dynamically.
 * This is useful if certain alerts need to be treated with more urgency based on context.
 */
public class PriorityAlertDecorator extends AlertDecorator {

    /** The amount to increase the priority by. */
    private final int extraPriority;

    /**
     * Creates a new alert with an increased priority.
     *
     * @param decoratedAlert the original alert to wrap
     * @param extraPriority  the amount to boost the priority (e.g., +1 or +2)
     */
    public PriorityAlertDecorator(Alert decoratedAlert, int extraPriority) {
        super(decoratedAlert);
        this.extraPriority = extraPriority;
    }

    @Override
    public int getPriority() {
        // Ensure priority doesn't exceed a max value (e.g., 5)
        return Math.min(super.getPriority() + extraPriority, 5);
    }
}
