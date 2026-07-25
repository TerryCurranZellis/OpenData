/*
 * Filename: OfgemConfiguration.java
 *
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.config.model.PluginEndpointDefinition;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;

/** Typed configuration used by the executable Ofgem plugin. */
public record OfgemConfiguration(
        PluginEndpointDefinition publicationEndpoint,
        String outputFilename,
        Duration connectTimeout,
        Duration requestTimeout,
        boolean archiveOriginalFile,
        Path workingDirectory,
        Path archiveDirectory) {

    public static final String ENDPOINT_NAME = "price-cap-publication";

    public OfgemConfiguration {
        Objects.requireNonNull(publicationEndpoint, "publicationEndpoint");
        outputFilename = requireText(outputFilename, "download.output-filename");
        Objects.requireNonNull(connectTimeout, "connectTimeout");
        Objects.requireNonNull(requestTimeout, "requestTimeout");
        Objects.requireNonNull(workingDirectory, "workingDirectory");
        Objects.requireNonNull(archiveDirectory, "archiveDirectory");
        if (connectTimeout.isZero() || connectTimeout.isNegative()) {
            throw new IllegalArgumentException("download.connect-timeout must be positive");
        }
        if (requestTimeout.isZero() || requestTimeout.isNegative()) {
            throw new IllegalArgumentException("download.request-timeout must be positive");
        }
    }

    public static OfgemConfiguration from(final PluginDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        if (!"ofgem".equalsIgnoreCase(definition.id())) {
            throw new IllegalArgumentException(
                    "Expected plugin id 'ofgem' but received '" + definition.id() + "'");
        }
        return new OfgemConfiguration(
                definition.requireEndpoint(ENDPOINT_NAME),
                value(definition, "download.output-filename", "ofgem-final-levelised-cap-rates.xlsx"),
                duration(definition, "download.connect-timeout", Duration.ofSeconds(30)),
                duration(definition, "download.request-timeout", Duration.ofSeconds(120)),
                bool(definition, "archive.original-file", true),
                Path.of(value(definition, "download.working-directory", "work/ofgem")),
                Path.of(value(definition, "archive.directory", "archive/ofgem")));
    }

    public Path downloadPath() {
        return workingDirectory.resolve(outputFilename).normalize();
    }

    private static String value(
            final PluginDefinition definition,
            final String name,
            final String defaultValue) {
        return definition.findProperty(name)
                .map(property -> property.value().trim())
                .filter(value -> !value.isEmpty())
                .orElse(defaultValue);
    }

    private static Duration duration(
            final PluginDefinition definition,
            final String name,
            final Duration defaultValue) {
        try {
            return Duration.parse(value(definition, name, defaultValue.toString()));
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException(
                    "Ofgem property '" + name + "' must be an ISO-8601 duration", exception);
        }
    }

    private static boolean bool(
            final PluginDefinition definition,
            final String name,
            final boolean defaultValue) {
        return switch (value(definition, name, Boolean.toString(defaultValue)).toLowerCase()) {
            case "true", "yes", "1", "on" -> true;
            case "false", "no", "0", "off" -> false;
            default -> throw new IllegalArgumentException(
                    "Ofgem property '" + name + "' must be a boolean");
        };
    }

    private static String requireText(final String value, final String name) {
        Objects.requireNonNull(value, name);
        final String result = value.trim();
        if (result.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return result;
    }
}
