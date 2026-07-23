package com.towermarsh.opendata.plugin;

import java.util.List;
import java.util.Optional;

/**
 * Provides metadata for installed OpenData plugins.
 *
 * <p>The Phase 1 implementation is backed by classpath properties files.
 * A later database-backed implementation can implement the same interface.</p>
 */
public interface PluginRegistry {

    /**
     * Lists all installed plugins in identifier order.
     *
     * @return immutable plugin list
     */
    List<PluginDescriptor> list();

    /**
     * Finds an installed plugin.
     *
     * @param pluginId plugin identifier
     * @return descriptor when installed
     */
    Optional<PluginDescriptor> find(String pluginId);

    /**
     * Requires an installed and enabled plugin.
     *
     * @param pluginId plugin identifier
     * @return enabled plugin descriptor
     * @throws PluginRegistryException when absent or disabled
     */
    PluginDescriptor requireEnabled(String pluginId);
}
