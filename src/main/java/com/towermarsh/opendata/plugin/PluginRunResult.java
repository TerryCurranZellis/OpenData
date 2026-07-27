/*
 * Filename: PluginRunResult.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Completed result for one selected plugin.
 * @param pluginId plugin identifier
 * @param runId plugin run identifier
 * @param status final plugin run status
 * @param startedAt time when plugin execution started
 * @param completedAt time when plugin execution completed
 * @param metrics row-level metrics returned by the plugin
 * @param errorMessage optional error message recorded for the run
 */
public record PluginRunResult(
        String pluginId,
        UUID runId,
        PluginRunStatus status,
        Instant startedAt,
        Instant completedAt,
        PluginMetrics metrics,
        Optional<String> errorMessage) {

    /** Validates and normalises record components. */
    public PluginRunResult {
        Objects.requireNonNull(pluginId, "pluginId");
        Objects.requireNonNull(runId, "runId");
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(startedAt, "startedAt");
        Objects.requireNonNull(completedAt, "completedAt");
        Objects.requireNonNull(metrics, "metrics");
        errorMessage = errorMessage == null ? Optional.empty() : errorMessage;
    }

    /**
     * Returns the total plugin execution duration.
     *
     * @return execution duration
     */
    public Duration duration() {
        return Duration.between(startedAt, completedAt);
    }

    /**
     * Indicates whether the plugin run completed without failure.
     *
     * @return {@code true} when the run succeeded or was a dry run
     */
    public boolean successful() {
        return status == PluginRunStatus.SUCCESS || status == PluginRunStatus.DRY_RUN;
    }
}
