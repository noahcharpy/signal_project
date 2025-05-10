package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
* An interface that defines how patient data should be generated.
 * * Each implementing class will decide what kind of data to simulate (like ECG, etc.).
 */
public interface PatientDataGenerator {
/**
 * Generates one data point for a specific patient.
 *
 * @param patientId      the ID of the patient
 * @param outputStrategy where the generated data should be sent
 */
    void generate(int patientId, OutputStrategy outputStrategy);
}
