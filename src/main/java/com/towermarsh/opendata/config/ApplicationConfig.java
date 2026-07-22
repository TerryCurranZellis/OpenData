package com.towermarsh.opendata.config;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.towermarsh.opendata.config.model.BootstrapConfig;
import com.towermarsh.opendata.config.model.PluginDefinition;

/**
 * Immutable configuration for one application execution.
 *
 * <p>Phase 1 loads the {@link PluginDefinition} from a plugin properties file.
 * A future database-backed loader can provide the same record without changing
 * plugin code.</p>
 *
 * @param bootstrap application bootstrap configuration
 * @param plugin structured selected plugin definition
 * @param runtimeOverrides invocation-only override values
 * @param overrideFile optional properties override file
 * @param dryRun whether persistent pipeline changes are disabled
 * @param verbose whether verbose logging is requested
 */
public record ApplicationConfig(
        BootstrapConfig bootstrap,
        PluginDefinition plugin,
        Map<String, String> runtimeOverrides,
        Optional<Path> overrideFile,
        boolean dryRun,
        boolean verbose) {

    public ApplicationConfig {
        Objects.requireNonNull(bootstrap, "bootstrap");
        Objects.requireNonNull(plugin, "plugin");
        runtimeOverrides = Map.copyOf(
                Objects.requireNonNull(runtimeOverrides, "runtimeOverrides"));
        overrideFile = overrideFile == null ? Optional.empty() : overrideFile;
    }

    public String pluginId() {
        return plugin.id();
    }
}
