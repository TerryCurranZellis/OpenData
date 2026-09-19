/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.initialise;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.plugin.octopusadjustment.OctopusAdjustmentPlugin;
import com.towermarsh.opendata.validation.PluginPropertyValues;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Typed configuration for the Octopus Energy adjustment plugin.
 *
 * @param accountNumber Octopus account number used as the filename prefix
 * @param inputDirectory source PDF directory
 * @param workingDirectory temporary working directory
 * @param archiveDirectory post-commit archive directory
 *
 * @author Terry Curran
 * @version 3.1.0
 * @since 3.1.0
 */
public record OctopusAdjustmentConfiguration(
        String accountNumber,
        Path inputDirectory,
        Path workingDirectory,
        Path archiveDirectory) {

    /** Property key for the Octopus account-number filename prefix. */
    public static final String PROP_ACCOUNT_NUMBER = "account.number";
    /** Property key for the source directory. */
    public static final String PROP_INPUT_DIRECTORY = "input.directory";
    /** Property key for the working directory. */
    public static final String PROP_WORKING_DIRECTORY = "working.directory";
    /** Property key for the archive directory. */
    public static final String PROP_ARCHIVE_DIRECTORY = "archive.directory";

    private static final Pattern SAFE_ACCOUNT_NUMBER = Pattern.compile("[A-Za-z0-9][A-Za-z0-9-]*");

    /** Validates and normalises record components. */
    public OctopusAdjustmentConfiguration {
        accountNumber = requireSafeAccountNumber(accountNumber);
        Objects.requireNonNull(inputDirectory, "inputDirectory");
        Objects.requireNonNull(workingDirectory, "workingDirectory");
        Objects.requireNonNull(archiveDirectory, "archiveDirectory");
    }

    /**
     * Builds typed adjustment configuration from a resolved plugin definition.
     *
     * @param definition resolved plugin definition
     * @return typed adjustment configuration
     * @since 3.1.0
     */
    public static OctopusAdjustmentConfiguration from(final PluginDefinition definition) {
        Objects.requireNonNull(definition, "definition");
        if (!OctopusAdjustmentPlugin.PLUGIN_ID.equalsIgnoreCase(definition.id())) {
            throw new IllegalArgumentException(
                    "Expected plugin id '" + OctopusAdjustmentPlugin.PLUGIN_ID
                    + "' but received '" + definition.id() + "'");
        }

        final var properties = new PluginPropertyValues(definition);
        return new OctopusAdjustmentConfiguration(
                properties.requiredText(PROP_ACCOUNT_NUMBER),
                properties.requiredPath(PROP_INPUT_DIRECTORY),
                properties.requiredPath(PROP_WORKING_DIRECTORY),
                properties.requiredPath(PROP_ARCHIVE_DIRECTORY));
    }

    private static String requireSafeAccountNumber(final String value) {
        Objects.requireNonNull(value, "accountNumber");
        final var result = value.trim();
        if (result.isEmpty()) {
            throw new IllegalArgumentException("accountNumber must not be blank");
        }
        if (result.indexOf('/') >= 0 || result.indexOf('\\') >= 0
                || !SAFE_ACCOUNT_NUMBER.matcher(result).matches()) {
            throw new IllegalArgumentException(
                    "accountNumber must contain only letters, digits and hyphens and must be safe as a filename prefix");
        }
        return result;
    }
}
