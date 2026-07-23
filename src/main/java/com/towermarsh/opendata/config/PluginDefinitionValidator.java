/*
 * Filename: PluginDefinitionValidator.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The author may be contacted by email to the following address:
 *
 * terry.curran@towermarsh.co.uk
 */

package com.towermarsh.opendata.config;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.towermarsh.opendata.config.model.DownloadStrategyType;
import com.towermarsh.opendata.config.model.PluginDefinition;

/**
 * Validates structural and cross-reference rules for plugin definitions.
 *
 * @author Terry Curran
 * @version 21 Jul 2026
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
