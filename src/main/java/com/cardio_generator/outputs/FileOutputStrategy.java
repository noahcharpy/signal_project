package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is used to save patient data into text files.
 * Each signal type (like HeartRate or ECG) gets its own file.
 * The files are saved in a folder defined by the user.
 */

public class FileOutputStrategy implements OutputStrategy {

    /**
     * The folder where output files will be stored.
     */
    private String baseDirectory; // Renamed BaseDirectory to baseDirectory to follow camelCase

    /**
     * Stores which file path corresponds to which signal label.
     * This avoids recalculating the file path every time.
     */
    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();// Renamed file_map to fileMap to follow camelCase

    /**
     * Creates a new FileOutputStrategy that writes data to the given folder.
     *
     * @param baseDirectory the directory where files should be saved
     */
    public FileOutputStrategy(String baseDirectory) {

        this.baseDirectory = baseDirectory;
    }

    /**
     * Writes patient data to a file. The file depends on the signal label (like "ECG").
     * If the folder or file doesn't exist yet, it's created automatically.
     *
     * @param patientId the ID of the patient
     * @param timestamp the time the measurement was taken
     * @param label     the type of data (e.g., "ECG", "BloodPressure")
     * @param data      the actual measurement value
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory and ensure it exists
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Set the filePath variable
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString()); // Renamed FilePath to filePath to follow camelCase

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(
                        Paths.get(filePath),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}