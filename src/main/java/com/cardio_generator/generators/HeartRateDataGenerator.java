package com.cardio_generator.generators;

import java.util.Random;
import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates random heart rate values for simulated patients.
 * Typical resting heart rate ranges from 60 to 100 bpm.
 */
public class HeartRateDataGenerator implements PatientDataGenerator {

    private static final Random random = new Random();

    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        // Generate a random heart rate between 45 and 135 for testing edge cases
        int heartRate = 45 + random.nextInt(91);  // Range: 45–135 bpm
        outputStrategy.output(patientId, System.currentTimeMillis(), "HeartRate", String.valueOf(heartRate));
    }
}
