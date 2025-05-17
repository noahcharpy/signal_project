package com.data_management;

import org.junit.jupiter.api.Test;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileDataReaderTest {

    @Test
    void testReadValidFile() throws IOException {
        // Create temporary output directory and file
        String dir = "test-output";
        Path path = Path.of(dir, "Saturation.txt");
        path.toFile().getParentFile().mkdirs();

        try (PrintWriter out = new PrintWriter(new FileWriter(path.toFile()))) {
            out.println("Patient ID: 1, Timestamp: 1714376789051, Label: Saturation, Data: 97%");
        }

        DataStorage storage = new DataStorage();
        FileDataReader reader = new FileDataReader(dir);
        reader.readData(storage);

        assertFalse(storage.getAllPatients().isEmpty());
    }

    @Test
    void testReadCorruptedLine() throws IOException {
        // Use a unique directory to isolate from other tests
        String dir = "test-output-corrupted";
        Path path = Path.of(dir, "Corrupted.txt");
        path.toFile().getParentFile().mkdirs();

        // Write a line that will trigger a parsing error
        try (PrintWriter out = new PrintWriter(new FileWriter(path.toFile()))) {
            out.println("Invalid Line");
        }

        // Read the file
        DataStorage storage = new DataStorage();
        FileDataReader reader = new FileDataReader(dir);
        reader.readData(storage);

        // Print for debugging if needed
        System.out.println("Patients in storage: " + storage.getAllPatients().size());

        // Assert nothing was added
        assertEquals(0, storage.getAllPatients().size());
    }
}
