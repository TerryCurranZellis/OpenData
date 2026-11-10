/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.time.Clock;
import java.util.Objects;
import java.util.UUID;

/**
 * Isolated dependencies and configuration for one plugin task.
 *
 * @param runId plugin run identifier
 * @param descriptor installed plugin descriptor
 * @param definition resolved plugin definition for this execution
 * @param database database resource manager available to the plugin
 * @param clock clock used for time-based operations
 * @param dryRun whether the plugin is executing in dry-run mode
 *
 * @author Terry Curran
 * @since 1.0.0
 * @version 4.1.0
 */
public record PluginExecutionContext(
        UUID runId,
        PluginDescriptor descriptor,
        PluginDefinition definition,
        DatabaseResourceManager database,
        Clock clock,
        boolean dryRun) {

    /**
     * Validates that all required per-run dependencies are present.
     *
     * @param runId unique identifier for this execution
     * @param descriptor selected installed plugin
     * @param definition resolved plugin configuration
     * @param database database access contract for the run
     * @param clock execution clock
     * @param dryRun whether writes must be suppressed
     */
    public PluginExecutionContext {
        Objects.requireNonNull(runId, "runId");
        Objects.requireNonNull(descriptor, "descriptor");
        Objects.requireNonNull(definition, "definition");
        Objects.requireNonNull(database, "database");
        Objects.requireNonNull(clock, "clock");
    }
}
