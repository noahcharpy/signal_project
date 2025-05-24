package com.alerts;

/**
 * A simple implementation of AlertTrigger that prints alerts to the console.
 * This is mainly useful for debugging or testing because it just shows the alert
 * directly in the terminal instead of saving it or sending it elsewhere.
 */
public class ConsoleAlertTrigger implements AlertTrigger {

    /**
     * Displays the alert details in the console.
     *
     * @param alert the alert object that needs to be triggered
     */
    @Override
    public void trigger(Alert alert) {
        System.out.println("ALERT for Patient " + alert.getPatientId()
                + ": " + alert.getCondition() + " at " + alert.getTimestamp());
    }
}
