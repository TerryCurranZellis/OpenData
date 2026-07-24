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

/** Completed result for one selected plugin. */
public record PluginRunResult(
        String pluginId,
        UUID runId,
        PluginRunStatus status,
        Instant startedAt,
        Instant completedAt,
        PluginMetrics metrics,
        Optional<String> errorMessage) {

    public PluginRunResult {
        Objects.requireNonNull(pluginId, "pluginId");
        Objects.requireNonNull(runId, "runId");
        Objects.requireNonNull(status, "status");
        Objects.requireNonNull(startedAt, "startedAt");
        Objects.requireNonNull(completedAt, "completedAt");
        Objects.requireNonNull(metrics, "metrics");
        errorMessage = errorMessage == null ? Optional.empty() : errorMessage;
    }

    public Duration duration() {
        return Duration.between(startedAt, completedAt);
    }

    public boolean successful() {
        return status == PluginRunStatus.SUCCESS || status == PluginRunStatus.DRY_RUN;
    }
}
