package com.alerts.decorators;

import com.alerts.Alert;

/**
 * Abstract decorator class for the Alert interface.
 * This class wraps an existing Alert instance and delegates all method calls to it.
 * Used to extend alert functionality dynamically using the Decorator Pattern.
 */
public abstract class AlertDecorator extends Alert {

    /** The alert instance that is being decorated. */
    protected Alert decoratedAlert;

    /**
     * Constructs a decorator that wraps a given alert.
     *
     * @param decoratedAlert the original alert to wrap
     */
    public AlertDecorator(Alert decoratedAlert) {
        super(decoratedAlert.getPatientId(), decoratedAlert.getCondition(), decoratedAlert.getTimestamp());
        this.decoratedAlert = decoratedAlert;
    }

    @Override
    public String getPatientId() {
        return decoratedAlert.getPatientId();
    }

    @Override
    public String getCondition() {
        return decoratedAlert.getCondition();
    }

    @Override
    public long getTimestamp() {
        return decoratedAlert.getTimestamp();
    }

    @Override
    public String getAlertType() {
        return decoratedAlert.getAlertType();
    }

    @Override
    public int getPriority() {
        return decoratedAlert.getPriority();
    }
}
