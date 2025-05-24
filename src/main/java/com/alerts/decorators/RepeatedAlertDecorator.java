package com.alerts.decorators;

import com.alerts.Alert;

/**
 * Decorator that adds metadata about how many times an alert has occurred.
 * <p>This can be used for repeated alerts to provide context or trigger special behaviors.</p>
 */
public class RepeatedAlertDecorator extends AlertDecorator {

    /** The number of times this alert condition has been repeated. */
    private final int repetitionCount;

    /**
     * Creates an alert that includes repetition information.
     *
     * @param decoratedAlert   the base alert to decorate
     * @param repetitionCount  how many times this alert has triggered
     */
    public RepeatedAlertDecorator(Alert decoratedAlert, int repetitionCount) {
        super(decoratedAlert);
        this.repetitionCount = repetitionCount;
    }

    /**
     * Returns the number of times this alert has been repeated.
     *
     * @return number of repetitions
     */
    public int getRepetitionCount() {
        return repetitionCount;
    }

    @Override
    public String getCondition() {
        // Append repetition count info to the condition string
        return super.getCondition() + " (repeated x" + repetitionCount + ")";
    }
}
