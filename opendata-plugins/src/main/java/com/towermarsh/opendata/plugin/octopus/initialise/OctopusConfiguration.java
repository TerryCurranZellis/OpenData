/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.initialise;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.validation.PluginPropertyValues;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Typed configuration for the Octopus plugin.
 *
 * <p>Holds the paths resolved from the plugin property definitions before the
 * ETL pipeline begins.
 *
 * @param inputDirectory directory containing Octopus Energy statement PDF files
 * @param workingDirectory temporary working directory used during processing
 * @param archiveDirectory directory receiving successfully processed statements
 *
 * @author Terry Curran
 * @version 2.0.0
 * @since 2.0.0
 */
public record OctopusConfiguration(
        Path inputDirectory,
        Path workingDirectory,
        Path archiveDirectory) {

    /** Property key for the PDF input directory. */
    public static final String PROP_INPUT_DIRECTORY = "input.directory";

    /** Property key for the working directory. */
    public static final String PROP_WORKING_DIRECTORY = "working.directory";

    /** Property key for the archive directory. */
    public static final String PROP_ARCHIVE_DIRECTORY = "archive.directory";

    /**
     * Validates record components.
     *
     * @since 2.0.0
     */
    public OctopusConfiguration {
        Objects.requireNonNull(inputDirectory, "inputDirectory");
        Objects.requireNonNull(workingDirectory, "workingDirectory");
        Objects.requireNonNull(archiveDirectory, "archiveDirectory");
    }

    /**
     * Builds typed Octopus configuration from a resolved plugin definition.
     *
     * @param definition resolved plugin definition
     * @return typed Octopus configuration
     * @since 2.0.0
     */
    public static OctopusConfiguration from(final PluginDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        if (!"octopus".equalsIgnoreCase(definition.id())) {
            throw new IllegalArgumentException(
                    "Expected plugin id 'octopus' but received '" + definition.id() + "'");
        }

        final var properties = new PluginPropertyValues(definition);
        return new OctopusConfiguration(
                properties.requiredPath(PROP_INPUT_DIRECTORY),
                properties.requiredPath(PROP_WORKING_DIRECTORY),
                properties.requiredPath(PROP_ARCHIVE_DIRECTORY));
    }
}
