package com.alerts;

import com.alerts.decorators.PriorityAlertDecorator;
import com.alerts.decorators.RepeatedAlertDecorator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Alert decorators (Decorator Pattern).
 * Ensures decorated alerts behave as expected when chaining functionality.
 */
class DecoratorTest {

    /**
     * Tests that PriorityAlertDecorator increases the base alert's priority.
     */
    @Test
    void testPriorityDecoratorAddsCorrectValue() {
        Alert base = new Alert("1", "Decorated Alert", System.currentTimeMillis()) {
            @Override
            public int getPriority() {
                return 2;
            }

            @Override
            public String getAlertType() {
                return "Mock";
            }
        };

        Alert decorated = new PriorityAlertDecorator(base, 3);

        assertEquals(5, decorated.getPriority());
        assertEquals("1", decorated.getPatientId());
        assertEquals("Decorated Alert", decorated.getCondition());
        assertEquals("Mock", decorated.getAlertType());
    }

    /**
     * Tests that RepeatedAlertDecorator appends repeat count to the condition.
     */
    @Test
    void testRepeatedAlertModifiesCondition() {
        Alert base = new Alert("2", "Heart Rate Irregularity", System.currentTimeMillis()) {
            @Override
            public String getAlertType() {
                return "Mock";
            }
        };

        Alert decorated = new RepeatedAlertDecorator(base, 2);

        assertEquals("Heart Rate Irregularity (repeated x2)", decorated.getCondition());
        assertEquals("2", decorated.getPatientId());
        assertEquals("Mock", decorated.getAlertType());
        assertEquals(0, decorated.getPriority()); // default priority
    }

    /**
     * Tests chaining Priority and Repeated decorators on the same alert.
     */
    @Test
    void testChainedDecorators() {
        Alert base = new Alert("3", "Low BP", System.currentTimeMillis()) {
            @Override
            public int getPriority() {
                return 1;
            }

            @Override
            public String getAlertType() {
                return "BP";
            }
        };

        Alert decorated = new RepeatedAlertDecorator(new PriorityAlertDecorator(base, 2), 3);

        assertEquals("Low BP (repeated x3)", decorated.getCondition());
        assertEquals(3, decorated.getPriority()); // base 1 + 2
        assertEquals("3", decorated.getPatientId());
        assertEquals("BP", decorated.getAlertType());
    }

    @Test
    void testZeroPriorityAdded() {
        Alert base = new Alert("10", "Test", System.currentTimeMillis()) {
            @Override public int getPriority() { return 1; }
        };
        Alert decorated = new PriorityAlertDecorator(base, 0);
        assertEquals(1, decorated.getPriority());
    }

    @Test
    void testNegativePriorityAdded() {
        Alert base = new Alert("10", "Negative", System.currentTimeMillis()) {
            @Override public int getPriority() { return 5; }
        };
        Alert decorated = new PriorityAlertDecorator(base, -3);
        assertEquals(2, decorated.getPriority()); // Allowing negative addition for now
    }

    @Test
    void testZeroRepeatCount() {
        Alert base = new Alert("5", "Stable", System.currentTimeMillis()) {};
        Alert decorated = new RepeatedAlertDecorator(base, 0);
        assertEquals("Stable (repeated x0)", decorated.getCondition());
    }

}
