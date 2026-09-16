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
 * @version 1.0.0
 */
public record PluginExecutionContext(
        UUID runId,
        PluginDescriptor descriptor,
        PluginDefinition definition,
        DatabaseResourceManager database,
        Clock clock,
        boolean dryRun) {

    /** Validates and normalises record components. */
    public PluginExecutionContext {
        Objects.requireNonNull(runId, "runId");
        Objects.requireNonNull(descriptor, "descriptor");
        Objects.requireNonNull(definition, "definition");
        Objects.requireNonNull(database, "database");
        Objects.requireNonNull(clock, "clock");
    }
}
