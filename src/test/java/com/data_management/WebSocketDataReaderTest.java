package com.data_management;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

public class WebSocketDataReaderTest {

    @Test
    public void testStartStreaming_initializesClient() {
        DataStorage dummyStorage = new DataStorage();
        WebSocketDataReader reader = new WebSocketDataReader();

        assertDoesNotThrow(() -> reader.startStreaming(dummyStorage));
    }

    @Test
    public void testStopStreaming_closesClientSafely() {
        DataStorage dummyStorage = new DataStorage();
        WebSocketDataReader reader = new WebSocketDataReader();

        reader.startStreaming(dummyStorage);
        assertDoesNotThrow(reader::stopStreaming);
    }
}
