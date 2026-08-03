/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example;

import com.towermarsh.opendata.config.model.PluginDefinition;
import java.net.URI;
import java.time.Duration;
import java.util.Objects;

/** Typed values derived from the generic plugin definition. */
public record ExamplePluginConfiguration(
        URI endpoint,
        Duration requestTimeout) {

    public ExamplePluginConfiguration {
        Objects.requireNonNull(endpoint, "endpoint");
        Objects.requireNonNull(requestTimeout, "requestTimeout");
        if (requestTimeout.isZero() || requestTimeout.isNegative()) {
            throw new IllegalArgumentException(
                    "requestTimeout must be positive");
        }
    }

    public static ExamplePluginConfiguration from(
            final PluginDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        return new ExamplePluginConfiguration(
                definition.requireEndpoint("source").uri(),
                Duration.ofSeconds(Integer.parseInt(
                        definition.requireProperty(
                                "request-timeout-seconds"))));
    }
}
