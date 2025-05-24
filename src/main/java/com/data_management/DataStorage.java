package com.data_management;

import java.util.*;
import com.alerts.AlertEvaluator;
import com.alerts.AlertTrigger;
import com.alerts.ConsoleAlertTrigger;

/**
 * Singleton class that manages storage and retrieval of patient data
 * within a healthcare monitoring system.
 */
public class DataStorage {

    /** The single shared instance of DataStorage (Singleton). */
    private static final DataStorage INSTANCE = new DataStorage();

    /** Stores patient objects indexed by their unique patient ID. */
    private final Map<Integer, Patient> patientMap;

    /**
     * Private constructor to ensure only one instance is created.
     */
    private DataStorage() {
        this.patientMap = new HashMap<>();
    }

    /**
     * Gets the singleton instance of the DataStorage class.
     *
     * @return the shared DataStorage instance
     */
    public static DataStorage getInstance() {
        return INSTANCE;
    }

    /**
     * Adds or updates patient data in the storage.
     *
     * @param patientId patient identifier
     * @param measurementValue the recorded value (e.g., 97%)
     * @param recordType the type of data (e.g., HeartRate, ECG)
     * @param timestamp the time of measurement
     */
    public void addPatientData(int patientId, String measurementValue, String recordType, long timestamp) {
        Patient patient = patientMap.get(patientId);
        if (patient == null) {
            patient = new Patient(patientId);
            patientMap.put(patientId, patient);
        }
        patient.addRecord(measurementValue, recordType, timestamp);
    }

    /**
     * Retrieves records from a specific time window.
     */
    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        Patient patient = patientMap.get(patientId);
        return patient != null ? patient.getRecords(startTime, endTime) : new ArrayList<>();
    }

    /**
     * Gets the patient object by ID.
     */
    public Patient getPatient(int id) {
        return patientMap.get(id);
    }

    /**
     * Gets all patients currently in storage.
     */
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patientMap.values());
    }

    /**
     * Clears all patients from storage (mainly for testing).
     */
    public void clear() {
        patientMap.clear();
    }
}
