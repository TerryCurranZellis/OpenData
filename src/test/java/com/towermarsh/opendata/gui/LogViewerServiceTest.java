/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.towermarsh.opendata.config.LoggingConfiguration;
import com.towermarsh.opendata.logging.LoggingManager;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests reading the active JUL file without closing the application's handlers.
 *
 * @author Terry Curran
 * @version 3.0.0
 */
class LogViewerServiceTest {

    @TempDir
    Path temporaryDirectory;

    @AfterEach
    void shutdownLogging() {
        LoggingManager.shutdown();
    }

    @Test
    void readsCurrentOpenDataLogAfterFlushingHandlers() throws Exception {
        LoggingManager.configure(
                new LoggingConfiguration(temporaryDirectory, 1024 * 1024, 3, true),
                false);
        LoggingManager.getLogger().info("Batch 5 log viewer test message");

        final var snapshot = new LogViewerService().load();

        assertTrue(snapshot.file().getFileName().toString().startsWith("opendata-"));
        assertTrue(snapshot.content().contains("Batch 5 log viewer test message"));
    }
}
