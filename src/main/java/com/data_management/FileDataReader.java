package com.data_management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * A reader that loads patient data from text files in a specified folder.
 * This is used to simulate reading previously saved data into the system.
 */
public class FileDataReader implements DataReader {

    private final String directoryPath;

    /**
     * Constructs a new FileDataReader for the specified directory.
     *
     * @param directoryPath path to the folder where .txt data files are stored
     */
    public FileDataReader(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    /**
     * Reads all patient data files in the given directory and loads
     * the records into the DataStorage system.
     *
     * @param storage the object where parsed records will be added
     */
    @Override
    public void readData(DataStorage storage) {
        File folder = new File(directoryPath);

        // Only look at .txt files in the directory
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));

        if (files == null) {
            System.err.println("No files found in: " + directoryPath);
            return;
        }

        for (File file : files) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    try {
                        // Expected format: "Patient ID: 3, Timestamp: 1714376789051, Label: Saturation, Data: 98%"
                        String[] parts = line.split(", ");

                        int patientId = Integer.parseInt(parts[0].split(": ")[1]);
                        long timestamp = Long.parseLong(parts[1].split(": ")[1]);
                        String recordType = parts[2].split(": ")[1];
                        String dataString = parts[3].split(": ")[1].replace("%", "").trim();

                        // Store the record in memory
                        storage.addPatientData(patientId, dataString, recordType, timestamp);

                    } catch (Exception e) {
                        // Skip lines that don't follow the expected format
                        System.err.println("Skipping line due to parsing error: " + line);
                    }
                }

            } catch (IOException e) {
                System.err.println("Error reading file: " + file.getName());
            }
        }
    }
}
