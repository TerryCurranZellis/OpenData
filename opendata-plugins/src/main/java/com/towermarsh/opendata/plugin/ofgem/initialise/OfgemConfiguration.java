/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem.initialise;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.config.model.PluginEndpointDefinition;
import com.towermarsh.opendata.validation.PluginPropertyValues;
import com.towermarsh.opendata.validation.ValidationRules;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;

/**
 * Typed configuration used by the executable Ofgem plugin.
 *
 * @param publicationEndpoint configured publication endpoint definition
 * @param outputFilename downloaded workbook file name
 * @param connectTimeout HTTP connection timeout
 * @param requestTimeout HTTP request timeout
 * @param archiveOriginalFile whether the downloaded workbook is archived after a write run
 * @param workingDirectory directory used for the active download
 * @param archiveDirectory directory containing archived workbooks
 *
 * @author Terry Curran
 * @version 2.0.0
 * @since 2.0.0
 */
public record OfgemConfiguration(
        PluginEndpointDefinition publicationEndpoint,
        String outputFilename,
        Duration connectTimeout,
        Duration requestTimeout,
        boolean archiveOriginalFile,
        Path workingDirectory,
        Path archiveDirectory) {

    /**
     * Name of the Ofgem publication endpoint in the plugin definition.
     *
     * @since 2.0.0
     */
    public static final String ENDPOINT_NAME = "price-cap-publication";

    /**
     * Validates and normalises record components.
     *
     * @since 2.0.0
     */
    public OfgemConfiguration {
        Objects.requireNonNull(publicationEndpoint, "publicationEndpoint");
        outputFilename = ValidationRules.requireText(
                outputFilename,
                "download.output-filename");
        connectTimeout = ValidationRules.requirePositive(
                connectTimeout,
                "download.connect-timeout");
        requestTimeout = ValidationRules.requirePositive(
                requestTimeout,
                "download.request-timeout");
        Objects.requireNonNull(workingDirectory, "workingDirectory");
        Objects.requireNonNull(archiveDirectory, "archiveDirectory");
    }

    /**
     * Builds typed Ofgem configuration from a resolved plugin definition.
     *
     * @param definition resolved plugin definition
     * @return typed Ofgem configuration
     * @since 2.0.0
     */
    public static OfgemConfiguration from(final PluginDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        if (!"ofgem".equalsIgnoreCase(definition.id())) {
            throw new IllegalArgumentException(
                    "Expected plugin id 'ofgem' but received '" + definition.id() + "'");
        }

        final PluginPropertyValues properties = new PluginPropertyValues(definition);
        return new OfgemConfiguration(
                definition.requireEndpoint(ENDPOINT_NAME),
                properties.text(
                        "download.output-filename",
                        "ofgem-final-levelised-cap-rates.xlsx"),
                properties.duration(
                        "download.connect-timeout",
                        Duration.ofSeconds(30)),
                properties.duration(
                        "download.request-timeout",
                        Duration.ofSeconds(120)),
                properties.booleanValue("archive.original-file", true),
                Path.of(properties.text(
                        "download.working-directory",
                        "work/ofgem")),
                Path.of(properties.text(
                        "archive.directory",
                        "archive/ofgem")));
    }

    /**
     * Returns the local workbook download path.
     *
     * @return local workbook download path
     * @since 2.0.0
     */
    public Path downloadPath() {
        return workingDirectory.resolve(outputFilename).normalize();
    }
}
