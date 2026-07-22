package com.towermarsh.opendata.config;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.towermarsh.opendata.cli.CommandLineArguments;
import com.towermarsh.opendata.config.model.BootstrapConfig;
import com.towermarsh.opendata.config.model.PluginDefinition;

/**
 * Creates the Phase 1 {@link ApplicationConfig}.
 */
public final class ApplicationConfigurationService {

    private final BootstrapConfig bootstrapConfig;
    private final PluginDefinitionLoader pluginDefinitionLoader;

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

        final String pluginId = arguments.pluginId()
                .orElseThrow(() -> new PluginDefinitionException(
                        "A plugin is required for an execution request."));

        final PluginDefinition plugin = pluginDefinitionLoader.load(
                pluginId,
                runtimeOverrides);

        return new ApplicationConfig(
                bootstrapConfig,
                plugin,
                runtimeOverrides,
                arguments.overrideFile(),
                arguments.dryRun(),
                arguments.verbose());
    }
}
