/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * @author Terry Curran
 * @version 1.0.0
 */
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
