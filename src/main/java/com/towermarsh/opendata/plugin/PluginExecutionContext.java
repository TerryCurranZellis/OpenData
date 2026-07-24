/*
 * Filename: PluginExecutionContext.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.time.Clock;
import java.util.Objects;
import java.util.UUID;

/** Isolated dependencies and configuration for one plugin task. */
public record PluginExecutionContext(
        UUID runId,
        PluginDescriptor descriptor,
        PluginDefinition definition,
        DatabaseResourceManager database,
        Clock clock,
        boolean dryRun) {

    public PluginExecutionContext {
        Objects.requireNonNull(runId, "runId");
        Objects.requireNonNull(descriptor, "descriptor");
        Objects.requireNonNull(definition, "definition");
        Objects.requireNonNull(database, "database");
        Objects.requireNonNull(clock, "clock");
    }
}
