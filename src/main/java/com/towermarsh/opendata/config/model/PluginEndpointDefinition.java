/*
 * Filename: PluginEndpointDefinition.java
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

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Complete definition of a source endpoint.
 *
 * @param name endpoint name unique within the plugin
 * @param type endpoint type
 * @param uri endpoint URI
 * @param httpMethod request method
 * @param contentFormat expected content format
 * @param strategy download strategy
 * @param enabled whether the endpoint is active
 * @param order execution order
 * @param credentialName optional credential reference name
 * @param headers non-secret request headers
 * @param queryParameters non-secret query parameters
 * @param linkDiscovery optional HTML link-discovery rules
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public record PluginEndpointDefinition(
        String name,
        EndpointType type,
        URI uri,
        HttpMethod httpMethod,
        DatasetFormat contentFormat,
        DownloadStrategyType strategy,
        boolean enabled,
        int order,
        Optional<String> credentialName,
        Map<String, String> headers,
        Map<String, String> queryParameters,
        Optional<LinkDiscoveryDefinition> linkDiscovery) {

    public PluginEndpointDefinition {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(uri, "uri");
        Objects.requireNonNull(httpMethod, "httpMethod");
        Objects.requireNonNull(contentFormat, "contentFormat");
        Objects.requireNonNull(strategy, "strategy");
        credentialName = credentialName == null ? Optional.empty() : credentialName;
        headers = Map.copyOf(Objects.requireNonNull(headers, "headers"));
        queryParameters = Map.copyOf(Objects.requireNonNull(queryParameters, "queryParameters"));
        linkDiscovery = linkDiscovery == null ? Optional.empty() : linkDiscovery;
    }
}
