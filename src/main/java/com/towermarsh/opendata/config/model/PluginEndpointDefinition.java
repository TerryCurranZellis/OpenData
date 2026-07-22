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
