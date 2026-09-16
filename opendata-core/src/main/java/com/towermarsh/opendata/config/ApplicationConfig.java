/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import java.util.Map;
import java.util.Objects;

import com.towermarsh.opendata.config.model.BootstrapConfig;
import com.towermarsh.opendata.config.model.PluginDefinition;

/**
 * Immutable configuration for one application execution.
 *
 * <p>The record is retained for focused configuration-service tests. Runtime
 * plugin execution is resolved from the persistent plugin registry and the
 * active configuration property source.</p>
 *
 * @param bootstrap application bootstrap configuration
 * @param plugin structured selected plugin definition
 * @param runtimeOverrides invocation-only override values
 * @param dryRun whether persistent pipeline changes are disabled
 * @param verbose whether verbose logging is requested
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public record ApplicationConfig(
        BootstrapConfig bootstrap,
        PluginDefinition plugin,
        Map<String, String> runtimeOverrides,
        boolean dryRun,
        boolean verbose) {

    /** 
     * Validates and normalises record components. 
     */
    public ApplicationConfig {
        Objects.requireNonNull(bootstrap, "bootstrap");
        Objects.requireNonNull(plugin, "plugin");
        runtimeOverrides = Map.copyOf(
                Objects.requireNonNull(runtimeOverrides, "runtimeOverrides"));
    }
    /**
     * Returns the configured plugin identifier for this execution.
     *
     * @return selected plugin identifier
     */
    public String pluginId() {
        return plugin.id();
    }
}
