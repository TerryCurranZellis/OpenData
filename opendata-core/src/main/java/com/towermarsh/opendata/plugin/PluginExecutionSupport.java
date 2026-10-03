/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.util.DurationFormatter;
import com.towermarsh.opendata.util.ExceptionMessages;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Shared logging and resource-shutdown helpers for plugin execution entry points.
 *
 * @author Terry Curran
 * @version 3.3.0
 * @since 3.3.0
 */
public final class PluginExecutionSupport {

    private PluginExecutionSupport() {
        // Utility class.
    }

    /**
     * Writes a consistent summary for one multi-plugin execution.
     *
     * @param logger destination logger
     * @param summary execution summary to log
     */
    public static void logSummary(
            final Logger logger,
            final PluginExecutionSummary summary) {
        summary.results().forEach(result -> logger.log(
                result.successful() ? Level.INFO : Level.SEVERE,
                "Plugin summary: id={0}, status={1}, duration={2}, read={3}, inserted={4}, "
                + "updated={5}, skipped={6}, error={7}",
                new Object[]{
                    result.pluginId(),
                    result.status().name(),
                    DurationFormatter.formatElapsed(result.duration()),
                    result.metrics().read(),
                    result.metrics().inserted(),
                    result.metrics().updated(),
                    result.metrics().skipped(),
                    result.errorMessage().orElse("")
                }));
        logger.log(Level.INFO,
                "Plugin execution complete; selected={0}, succeeded={1}, failed={2}",
                new Object[]{summary.results().size(), summary.succeeded(), summary.failed()});
    }

    /**
     * Closes one database resource while preserving the original failure path.
     *
     * @param logger destination logger
     * @param database database resource to close
     */
    public static void closeDatabase(
            final Logger logger,
            final DatabaseResourceManager database) {
        if (database == null) {
            return;
        }
        try {
            database.close();
        } catch (RuntimeException exception) {
            logger.log(Level.SEVERE,
                    "Database shutdown failed: {0}",
                    ExceptionMessages.rootCauseMessage(exception));
            logger.log(Level.FINE, "Database shutdown failure details.", exception);
        }
    }
}
