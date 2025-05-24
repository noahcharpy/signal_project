package com.data_management;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import java.net.URISyntaxException;

class WebSocketClientDataReaderTest {

    @Test
    void testOnMessageParsing() throws URISyntaxException {
        DataStorage storageMock = mock(DataStorage.class);
        WebSocketClientDataReader reader = new WebSocketClientDataReader("ws://localhost:8080", storageMock);

        String testMessage = "1,1714376789051,HeartRate,85";

        reader.onMessage(testMessage);

        verify(storageMock, times(1)).addPatientData(1, "85", "HeartRate", 1714376789051L);
    }
}
