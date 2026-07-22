package com.towermarsh.opendata.config;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.towermarsh.opendata.config.model.DownloadStrategyType;
import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.config.model.PluginEndpointDefinition;

/**
 * Validates structural and cross-reference rules for plugin definitions.
 */
public final class PluginDefinitionValidator {

    private static final Pattern PLUGIN_ID =
            Pattern.compile("[a-z][a-z0-9-]{1,99}");

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
        for (PluginEndpointDefinition endpoint : definition.endpoints()) {
            if (!endpointNames.add(endpoint.name().toLowerCase())) {
                throw new PluginDefinitionException(
                        "Duplicate endpoint name: " + endpoint.name());
            }

            endpoint.credentialName().ifPresent(name -> {
                if (!definition.credentials().containsKey(name.toLowerCase())) {
                    throw new PluginDefinitionException(
                            "Endpoint '%s' refers to unknown credential '%s'."
                                    .formatted(endpoint.name(), name));
                }
            });

            if (endpoint.strategy() == DownloadStrategyType.HTML_LINK_DISCOVERY
                    && endpoint.linkDiscovery().isEmpty()) {
                throw new PluginDefinitionException(
                        "Endpoint '%s' requires link-discovery rules."
                                .formatted(endpoint.name()));
            }
        }
    }
}
