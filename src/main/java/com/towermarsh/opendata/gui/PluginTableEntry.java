/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import com.towermarsh.opendata.plugin.PluginRunStatus;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Read-only data required to populate one row in the JavaFX plugin table.
 *
 * <p>The record deliberately contains no JavaFX properties. It is produced by
 * the GUI read service on a worker thread and converted to {@link PluginRow}
 * instances only when the result reaches the JavaFX application thread.</p>
 *
 * @param pluginId registered plugin identifier
 * @param description registered plugin description
 * @param enabled whether the registered plugin is enabled
 * @param lastRunStatus most recent run status, or empty when never run
 * @param lastRunStartedAtUtc most recent run start time in UTC, or empty when
 * never run
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public record PluginTableEntry(
        String pluginId,
        String description,
        boolean enabled,
        Optional<PluginRunStatus> lastRunStatus,
        Optional<LocalDateTime> lastRunStartedAtUtc) {

    /** Validates record components. */
    public PluginTableEntry {
        pluginId = Objects.requireNonNull(pluginId, "pluginId").trim();
        description = description == null ? "" : description.trim();
        lastRunStatus = Objects.requireNonNull(lastRunStatus, "lastRunStatus");
        lastRunStartedAtUtc = Objects.requireNonNull(lastRunStartedAtUtc, "lastRunStartedAtUtc");
        if (pluginId.isBlank()) {
            throw new IllegalArgumentException("pluginId must not be blank.");
        }
        if (lastRunStatus.isEmpty() != lastRunStartedAtUtc.isEmpty()) {
            throw new IllegalArgumentException(
                    "lastRunStatus and lastRunStartedAtUtc must either both be present or both be empty.");
        }
    }
}
