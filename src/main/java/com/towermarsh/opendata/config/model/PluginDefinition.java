/*
 * Filename: PluginDefinition.java
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

package com.towermarsh.opendata.config.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Structured Phase 1 definition parsed from a plugin properties file.
 *
 * @param id stable command-line plugin identifier
 * @param displayName human-readable name
 * @param description plugin description
 * @param implementationClass fully qualified implementation class
 * @param enabled whether the plugin may execute
 * @param configurationVersion configuration version
 * @param datasetId logical dataset identifier
 * @param endpoints configured source endpoints
 * @param properties plugin-specific typed properties
 * @param credentials secret references
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public record PluginDefinition(
        String id,
        String displayName,
        String description,
        String implementationClass,
        boolean enabled,
        int configurationVersion,
        String datasetId,
        List<PluginEndpointDefinition> endpoints,
        Map<String, PluginPropertyDefinition> properties,
        Map<String, CredentialReference> credentials) {

    public PluginDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(displayName, "displayName");
        description = description == null ? "" : description;
        Objects.requireNonNull(implementationClass, "implementationClass");
        Objects.requireNonNull(datasetId, "datasetId");
        endpoints = List.copyOf(Objects.requireNonNull(endpoints, "endpoints"));
        properties = Map.copyOf(Objects.requireNonNull(properties, "properties"));
        credentials = Map.copyOf(Objects.requireNonNull(credentials, "credentials"));
    }

    public PluginEndpointDefinition requireEndpoint(final String name) {
        return endpoints.stream()
                .filter(endpoint -> endpoint.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Plugin '%s' does not define endpoint '%s'.".formatted(id, name)));
    }

    public Optional<PluginPropertyDefinition> findProperty(final String name) {
        return Optional.ofNullable(properties.get(name.toLowerCase(java.util.Locale.ROOT)));
    }

    public String requireProperty(final String name) {
        return findProperty(name)
                .map(PluginPropertyDefinition::value)
                .filter(value -> !value.isBlank())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Plugin '%s' does not define property '%s'.".formatted(id, name)));
    }
}
