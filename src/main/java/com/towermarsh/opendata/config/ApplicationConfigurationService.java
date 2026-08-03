/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import java.util.Map;
import java.util.Objects;

import com.towermarsh.opendata.cli.CommandLineArguments;
import com.towermarsh.opendata.config.model.BootstrapConfig;

/**
 * Creates the Phase 1 {@link ApplicationConfig}.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class ApplicationConfigurationService {

    private final BootstrapConfig bootstrapConfig;
    private final PluginDefinitionLoader pluginDefinitionLoader;

    /**
     * Creates a service that resolves application configuration for one invocation.
     *
     * @param bootstrapConfig loaded bootstrap configuration
     * @param pluginDefinitionLoader loader used to resolve plugin definitions
     */
    public ApplicationConfigurationService(
            final BootstrapConfig bootstrapConfig,
            final PluginDefinitionLoader pluginDefinitionLoader) {

        this.bootstrapConfig = Objects.requireNonNull(
                bootstrapConfig,
                "bootstrapConfig");
        this.pluginDefinitionLoader = Objects.requireNonNull(
                pluginDefinitionLoader,
                "pluginDefinitionLoader");
    }

    /**
     * Resolves structured configuration for one command invocation.
     *
     * @param arguments parsed command-line arguments
     * @param runtimeOverrides invocation-only values
     * @return immutable application configuration
     */
    public ApplicationConfig resolve(
            final CommandLineArguments arguments,
            final Map<String, String> runtimeOverrides) {

        final var pluginId = arguments.pluginIds().stream().findFirst()
                .orElseThrow(() -> new PluginDefinitionException(
                        "A named plugin is required for an execution request."));

        final var plugin = pluginDefinitionLoader.load(
                pluginId,
                runtimeOverrides);

        return new ApplicationConfig(
                bootstrapConfig,
                plugin,
                runtimeOverrides,
                arguments.dryRun(),
                arguments.verbose());
    }
}
