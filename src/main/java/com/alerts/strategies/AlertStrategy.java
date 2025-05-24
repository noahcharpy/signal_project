package com.alerts.strategies;

import com.alerts.AlertTrigger;
import com.data_management.PatientRecord;

import java.util.List;

/**
 * Interface for alert strategies based on different health data types.
 */
public interface AlertStrategy {

    /**
     * Evaluates records and triggers alerts based on health conditions.
     * Each strategy internally determines the appropriate AlertFactory.
     *
     * @param patientId the patient's ID
     * @param records   the records of a specific type (e.g., BloodPressure, ECG)
     * @param trigger   the alert trigger used to dispatch alerts
     */
    void checkAlert(String patientId, List<PatientRecord> records, AlertTrigger trigger);
}
