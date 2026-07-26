package com.towermarsh.opendata.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class ExecutionStatusTest {

    @Test
    void exposesHumanReadableDescriptions() {
        assertEquals("Successful", ExecutionStatus.SUCCESS.displayName());
        assertEquals(
                "One or more plugins failed",
                ExecutionStatus.PLUGIN_FAILURE.displayName());
        assertEquals("Application error", ExecutionStatus.APPLICATION_FAILURE.displayName());
    }
}
