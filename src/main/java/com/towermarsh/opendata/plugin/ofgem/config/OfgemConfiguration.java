/*
 * Filename: OfgemConfiguration.java
 *
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem.config;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.config.model.PluginEndpointDefinition;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;

/** Typed configuration used by the executable Ofgem plugin.
 * @param publicationEndpoint configured publication endpoint definition
 * @param outputFilename downloaded workbook file name
 * @param connectTimeout HTTP connection timeout
 * @param requestTimeout HTTP request timeout
 * @param archiveOriginalFile whether the downloaded workbook is archived after a write run
 * @param workingDirectory directory used for the active download
 * @param archiveDirectory directory containing archived workbooks
 */
public record OfgemConfiguration(
        PluginEndpointDefinition publicationEndpoint,
        String outputFilename,
        Duration connectTimeout,
        Duration requestTimeout,
        boolean archiveOriginalFile,
        Path workingDirectory,
        Path archiveDirectory) {

    public static final String ENDPOINT_NAME = "price-cap-publication";

    /** Validates and normalises record components. */
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

    /**
     * Builds typed Ofgem configuration from a resolved plugin definition.
     *
     * @param definition resolved plugin definition
     * @return typed Ofgem configuration
     */
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

    /**
     * Returns the local workbook download path.
     *
     * @return local workbook download path
     */
    public Path downloadPath() {
        return workingDirectory.resolve(outputFilename).normalize();
    }

    /**
     * Returns a plugin property value or a default.
     *
     * @param definition plugin definition
     * @param name property name
     * @param defaultValue fallback value
     * @return resolved property value
     */
    private static String value(
            final PluginDefinition definition,
            final String name,
            final String defaultValue) {
        return definition.findProperty(name)
                .map(property -> property.value().trim())
                .filter(value -> !value.isEmpty())
                .orElse(defaultValue);
    }

    /**
     * Returns an ISO-8601 duration property or a default.
     *
     * @param definition plugin definition
     * @param name property name
     * @param defaultValue fallback value
     * @return parsed duration
     */
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

    /**
     * Returns a boolean property or a default.
     *
     * @param definition plugin definition
     * @param name property name
     * @param defaultValue fallback value
     * @return parsed boolean value
     */
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

    /**
     * Returns a required non-blank text value.
     *
     * @param value value to validate
     * @param name field name for error reporting
     * @return trimmed text value
     */
    private static String requireText(final String value, final String name) {
        Objects.requireNonNull(value, name);
        final String result = value.trim();
        if (result.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return result;
    }
}
