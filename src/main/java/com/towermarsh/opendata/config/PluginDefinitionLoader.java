package com.towermarsh.opendata.config;

import java.util.Map;

import com.towermarsh.opendata.config.model.PluginDefinition;

/**
 * Loads a structured plugin definition from a storage-specific representation.
 */
public interface PluginDefinitionLoader {

    /**
     * Loads a plugin definition.
     *
     * @param pluginId selected plugin id
     * @param overrides optional merged override values
     * @return structured plugin definition
     */
    PluginDefinition load(String pluginId, Map<String, String> overrides);
}
