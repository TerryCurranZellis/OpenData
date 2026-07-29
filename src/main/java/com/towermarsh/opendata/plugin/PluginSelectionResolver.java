/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import com.towermarsh.opendata.cli.CommandLineArguments;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** Resolves named plugins or every enabled plugin from the registry.  *
* @author Terry Curran
* @version 17 July 2026
*/
public final class PluginSelectionResolver {

    /**
     *
     * @param arguments
     * @param registry
     * @return
     */
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
        final List<String> requested = arguments.pluginIds().stream()
                .map(PluginSelectionResolver::canonicalId)
                .toList();
        if (new LinkedHashSet<>(requested).size() != requested.size()) {
            throw new PluginRegistryException("A plugin was selected more than once.");
        }
        return requested.stream().map(registry::requireEnabled).toList();
    }

    private static String canonicalId(final String pluginId) {
        return pluginId.trim().toLowerCase(Locale.ROOT);
    }
}
