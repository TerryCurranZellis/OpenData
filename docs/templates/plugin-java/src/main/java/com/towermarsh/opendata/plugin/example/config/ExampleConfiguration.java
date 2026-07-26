/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example.config;

import com.towermarsh.opendata.config.model.PluginDefinition;
import java.net.URI;
import java.nio.file.Path;
import java.util.Objects;

/** Typed configuration for the example plugin. */
public record ExampleConfiguration(URI sourceUri, Path downloadPath) {

    public ExampleConfiguration {
        Objects.requireNonNull(sourceUri, "sourceUri");
        Objects.requireNonNull(downloadPath, "downloadPath");
    }

    public static ExampleConfiguration from(final PluginDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        return new ExampleConfiguration(
                definition.requireEndpoint("source").uri(),
                Path.of(definition.requireProperty("download-path")).normalize());
    }
}
