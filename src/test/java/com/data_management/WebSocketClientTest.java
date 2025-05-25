package com.data_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URI;

import static org.mockito.Mockito.*;

public class WebSocketClientTest {

    private DataStorage mockStorage;
    private WebSocketClient client;

    @BeforeEach
    public void setUp() throws Exception {
        mockStorage = mock(DataStorage.class);
        client = new WebSocketClient(new URI("ws://localhost:1234"), mockStorage);
    }

    @Test
    public void testOnMessage_withValidMessage() {
        String validMessage = "123|heartRate|98.6|1716632543000";

        client.onMessage(validMessage);

        verify(mockStorage, times(1)).addPatientData(
                eq(123),
                eq("98.6"),
                eq("heartRate"),
                eq(1716632543000L)
        );
    }

    @Test
    public void testOnMessage_withInvalidFormat() {
        String badMessage = "bad|format|only";

        client.onMessage(badMessage);

        verify(mockStorage, never()).addPatientData(anyInt(), anyString(), anyString(), anyLong());
    }

    @Test
    public void testOnMessage_withInvalidNumbers() {
        String badNumberMessage = "abc|heartRate|notANumber|badTimestamp";

        client.onMessage(badNumberMessage);

        verify(mockStorage, never()).addPatientData(anyInt(), anyString(), anyString(), anyLong());
    }
}
