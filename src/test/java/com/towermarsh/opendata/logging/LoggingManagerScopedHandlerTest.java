/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.towermarsh.opendata.config.LoggingConfiguration;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Verifies the Batch 6 invariant that runtime root-handler reconfiguration does
 * not remove a handler scoped to the OpenData application logger.
 *
 * @author Terry Curran
 * @version 3.1.0
 */
class LoggingManagerScopedHandlerTest {

    @TempDir
    Path temporaryDirectory;

    private Handler scopedHandler;

    @AfterEach
    void cleanupLogging() {
        if (scopedHandler != null) {
            LoggingManager.getLogger().removeHandler(scopedHandler);
            scopedHandler.close();
        }
        LoggingManager.shutdown();
    }

    @Test
    void runtimeConfigurePreservesAndUsesApplicationScopedHandler() throws Exception {
        final var records = new AtomicInteger();
        scopedHandler = new Handler() {
            @Override
            public void publish(final LogRecord record) {
                if (isLoggable(record)
                        && "scoped handler survives root reconfiguration"
                                .equals(record.getMessage())) {
                    records.incrementAndGet();
                }
            }

            @Override
            public void flush() {
                // Nothing buffered.
            }

            @Override
            public void close() {
                // Nothing to release.
            }
        };
        LoggingManager.getLogger().addHandler(scopedHandler);

        LoggingManager.configure(
                new LoggingConfiguration(temporaryDirectory, 1024 * 1024, 3, true),
                false);

        assertTrue(Arrays.asList(LoggingManager.getLogger().getHandlers())
                .contains(scopedHandler));

        final Logger child = Logger.getLogger("com.towermarsh.opendata.plugin.batch6-test");
        child.info("scoped handler survives root reconfiguration");
        assertEquals(1, records.get());
    }
}
