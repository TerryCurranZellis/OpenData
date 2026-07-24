/*
 * Filename: PluginSelectionResolver.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import com.towermarsh.opendata.cli.CommandLineArguments;
import java.util.List;
import java.util.Objects;

/** Resolves named plugins or every enabled plugin from the registry. */
public final class PluginSelectionResolver {
    public List<PluginDescriptor> resolve(
            final CommandLineArguments arguments,
            final PluginRegistry registry) {
        Objects.requireNonNull(arguments, "arguments");
        Objects.requireNonNull(registry, "registry");
        if (arguments.allPluginsRequested()) {
            final List<PluginDescriptor> enabled = registry.list().stream()
                    .filter(PluginDescriptor::enabled)
                    .toList();
            if (enabled.isEmpty()) {
                throw new PluginRegistryException("No enabled plugins are installed.");
            }
            return enabled;
        }
        return arguments.pluginIds().stream().map(registry::requireEnabled).toList();
    }
}
