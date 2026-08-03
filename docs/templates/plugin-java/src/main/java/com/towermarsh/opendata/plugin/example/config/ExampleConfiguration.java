/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example.config;

import com.towermarsh.opendata.config.model.PluginDefinition;
import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;

/** Typed configuration for the example provider. */
public record ExampleConfiguration(
        URI sourceUri,
        Path downloadPath,
        Duration requestTimeout,
        long maximumBytes) {

    public ExampleConfiguration {
        Objects.requireNonNull(sourceUri, "sourceUri");
        downloadPath = Objects.requireNonNull(
                downloadPath, "downloadPath").toAbsolutePath().normalize();
        Objects.requireNonNull(requestTimeout, "requestTimeout");
        if (requestTimeout.isZero() || requestTimeout.isNegative()) {
            throw new IllegalArgumentException(
                    "requestTimeout must be positive");
        }
        if (maximumBytes < 1) {
            throw new IllegalArgumentException(
                    "maximumBytes must be positive");
        }
    }

    public static ExampleConfiguration from(
            final PluginDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        return new ExampleConfiguration(
                definition.requireEndpoint("source").uri(),
                Path.of(definition.requireProperty("download-path")),
                Duration.ofSeconds(Long.parseLong(
                        definition.requireProperty(
                                "request-timeout-seconds"))),
                Long.parseLong(definition.requireProperty("maximum-bytes")));
    }
}
