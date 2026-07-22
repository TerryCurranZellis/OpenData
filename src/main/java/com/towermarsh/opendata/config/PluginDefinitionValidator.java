package com.towermarsh.opendata.config;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.towermarsh.opendata.config.model.DownloadStrategyType;
import com.towermarsh.opendata.config.model.PluginDefinition;

/**
 * Validates structural and cross-reference rules for plugin definitions.
 */
public final class PluginDefinitionValidator {

    // plugin id must start with a letter
    private static final Pattern PLUGIN_ID = Pattern.compile("[a-z][a-z0-9-]{1,99}");

    /**
     * validate plugin 
     * @param definition the plugin definition
     */
    public void validate(final PluginDefinition definition) {
        if (!PLUGIN_ID.matcher(definition.id()).matches()) {
            throw new PluginDefinitionException(
                    "Invalid plugin id: " + definition.id());
        }

        if (!definition.enabled()) {
            throw new PluginDefinitionException(
                    "Plugin is disabled: " + definition.id());
        }

        if (definition.configurationVersion() < 1) {
            throw new PluginDefinitionException(
                    "plugin.configuration-version must be at least 1.");
        }

        if (definition.endpoints().isEmpty()) {
            throw new PluginDefinitionException(
                    "Plugin must define at least one endpoint.");
        }

        final Set<String> endpointNames = new HashSet<>();
        definition.endpoints().stream().map((var endpoint) -> {
            if (!endpointNames.add(endpoint.name().toLowerCase())) {
                throw new PluginDefinitionException(
                        "Duplicate endpoint name: " + endpoint.name());
            }
            return endpoint;
        }).map(endpoint -> {
            endpoint.credentialName().ifPresent(name -> {
                if (!definition.credentials().containsKey(name.toLowerCase())) {
                    throw new PluginDefinitionException(
                            "Endpoint '%s' refers to unknown credential '%s'."
                                    .formatted(endpoint.name(), name));
                }
            });
            return endpoint;
        }).filter(endpoint -> (endpoint.strategy() == DownloadStrategyType.HTML_LINK_DISCOVERY
                && endpoint.linkDiscovery().isEmpty())).forEachOrdered(endpoint -> {
                    throw new PluginDefinitionException(
                            "Endpoint '%s' requires link-discovery rules."
                                    .formatted(endpoint.name()));
        });
    }
}
