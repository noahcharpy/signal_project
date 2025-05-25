package com.data_management;

import java.io.IOException;

public interface DataReader {
    /**
     * Reads data from a specified source and stores it in the data storage.
     *
     * @param dataStorage the storage where data will be stored
     * @throws IOException if there is an error reading the data
     */
    void readData(DataStorage dataStorage) throws IOException;

    /**
     * Starts reading patient data from a real-time source such as a WebSocket.
     * The implementation should continuously receive and store data in real time.
     *
     * @param storage the storage object where streaming data will be stored
     */
    void startStreaming(DataStorage storage);
}