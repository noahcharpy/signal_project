package com.data_management;

import org.junit.jupiter.api.Test;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the FileDataReader class which reads patient data from text files.
 * It validates both successful reads and handling of corrupted input.
 */
class FileDataReaderTest {

    /**
     * Tests whether valid data from a file is read correctly into DataStorage.
     */
    @Test
    void testReadValidFile() throws IOException {
        // Arrange: Setup a mock data directory and file
        String dir = "test-output";
        Path path = Path.of(dir, "Saturation.txt");
        path.toFile().getParentFile().mkdirs();

        try (PrintWriter out = new PrintWriter(new FileWriter(path.toFile()))) {
            out.println("Patient ID: 1, Timestamp: 1714376789051, Label: Saturation, Data: 97%");
        }

        // Act: Use the reader to load data into the singleton DataStorage
        DataStorage storage = DataStorage.getInstance(); // Singleton version
        storage.clear();  // Ensure no leftover data from other tests
        FileDataReader reader = new FileDataReader(dir);
        reader.readData(storage);

        // Assert: The data should now exist in the singleton
        assertFalse(storage.getAllPatients().isEmpty(), "Expected non-empty storage after reading valid file");
    }

    /**
     * Tests the behavior when reading a corrupted line.
     * Ensures the system handles bad input gracefully without crashing.
     */
    @Test
    void testReadCorruptedLine() throws IOException {
        // Arrange: Create a separate directory and invalid data file
        String dir = "test-output-corrupted";
        Path path = Path.of(dir, "Corrupted.txt");
        path.toFile().getParentFile().mkdirs();

        try (PrintWriter out = new PrintWriter(new FileWriter(path.toFile()))) {
            out.println("Invalid Line");
        }

        // Act: Attempt to read the corrupted file
        DataStorage storage = DataStorage.getInstance(); // Singleton version
        storage.clear();  // Ensure clean state
        FileDataReader reader = new FileDataReader(dir);
        reader.readData(storage);

        // Assert: Corrupted line should not result in any patient being stored
        assertEquals(0, storage.getAllPatients().size(), "Expected no patients to be stored after reading corrupted line");
    }
}
