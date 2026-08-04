/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example.config;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.validation.PluginPropertyValues;
import com.towermarsh.opendata.validation.ValidationRules;
import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;

/**
 * Typed configuration for the example provider.
 *
 * @param sourceUri provider source URI
 * @param downloadPath local download path
 * @param requestTimeout request timeout
 * @param maximumBytes maximum accepted response size
 * @since 2.0.0
 */
public record ExampleConfiguration(
        URI sourceUri,
        Path downloadPath,
        Duration requestTimeout,
        long maximumBytes) {

    /**
     * Validates record components.
     *
     * @since 2.0.0
     */
    public ExampleConfiguration {
        Objects.requireNonNull(sourceUri, "sourceUri");
        downloadPath = Objects.requireNonNull(
                downloadPath, "downloadPath").toAbsolutePath().normalize();
        requestTimeout = ValidationRules.requirePositive(
                requestTimeout, "request-timeout");
        if (maximumBytes < 1) {
            throw new IllegalArgumentException(
                    "maximum-bytes must be positive");
        }
    }

    /**
     * Builds typed example configuration.
     *
     * @param definition resolved plugin definition
     * @return example configuration
     * @since 2.0.0
     */
    public static ExampleConfiguration from(
            final PluginDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        if (!"example".equalsIgnoreCase(definition.id())) {
            throw new IllegalArgumentException(
                    "Expected plugin id 'example' but received '"
                            + definition.id() + "'");
        }

        final var properties = new PluginPropertyValues(definition);
        return new ExampleConfiguration(
                definition.requireEndpoint("source").uri(),
                properties.requiredPath("download-path"),
                Duration.ofSeconds(properties.integer(
                        "request-timeout-seconds", 60)),
                properties.longValue("maximum-bytes", 10_000_000L));
    }
}
