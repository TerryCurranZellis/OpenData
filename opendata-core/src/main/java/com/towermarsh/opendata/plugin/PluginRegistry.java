/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

import java.util.List;
import java.util.Optional;

/**
 * Provides metadata for OpenData plugins available to a particular operation.
 *
 * <p>{@link JdbcPluginRegistry} is authoritative for administration and
 * execution. {@link ClasspathPluginRegistry} is the packaged registration
 * catalogue used by {@code --register} when no external file is supplied.</p>
  *
 * @author Terry Curran
 * @version 2.0.0
 */
public interface PluginRegistry {

    /**
     * Lists all plugins in identifier order.
     *
     * @return immutable plugin list
     */
    List<PluginDescriptor> list();

    /**
     * Finds an plugin.
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
