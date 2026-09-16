/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import com.towermarsh.opendata.validation.ValidationRules;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Minimal bootstrap properties required before database-backed configuration
 * can be loaded.
 *
 * @param applicationVersion configured application version string
 * @param databaseUrl SQL Server JDBC URL
 * @param databaseUser SQL Server login name
 * @param databasePassword SQL Server password in plain text after decryption
 * @param useDatabaseProperties whether runtime properties are loaded from the
 * database
 *
 * @author Terry Curran
 * @version 2.1
 */
public record ApplicationBootstrapProperties(
        String applicationVersion,
        String databaseUrl,
        String databaseUser,
        String databasePassword,
        boolean useDatabaseProperties) {

    /**
     * settings for SQL Server JDBC
     */
    private static final String DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    /**
     * Settings for database pool
     */
    private static final String POOL_NAME = "OpenData";
    private static final int MAX_TOTAL = 8;
    private static final int MAX_IDLE = 8;
    private static final int MIN_IDLE = 1;
    private static final int MAX_WAIT_SECONDS = 30;
    /**
     * test query to validate database access
     */
    private static final String VALIDATION_QUERY = "SELECT 1";

    /**
     * Validates and normalises record components.
     *
     * @param applicationVersion configured application version string
     * @param databaseUrl SQL Server JDBC URL
     * @param databaseUser SQL Server login name
     * @param databasePassword SQL Server password in plain text after
     * decryption
     * @param useDatabaseProperties whether runtime properties are loaded from
     * the database
     */
    public ApplicationBootstrapProperties {
        applicationVersion = requireText(applicationVersion, "applicationVersion");
        databaseUrl = requireText(databaseUrl, "databaseUrl");
        databaseUser = requireText(databaseUser, "databaseUser");
        Objects.requireNonNull(databasePassword, "databasePassword");
    }

    /**
     * Builds the database pool configuration used to reach the configuration
     * store itself.
     *
     * @return bootstrap SQL Server pool configuration
     */
    public DatabasePoolConfiguration toDatabasePoolConfiguration() {
        return new DatabasePoolConfiguration(
                DRIVER_CLASS,
                databaseUrl,
                databaseUser,
                databasePassword,
                POOL_NAME,
                MAX_TOTAL,
                MAX_IDLE,
                MIN_IDLE,
                java.time.Duration.ofSeconds(MAX_WAIT_SECONDS),
                VALIDATION_QUERY);
    }

    /**
     * Serialises the bootstrap properties back to file format.
     *
     * @param encryptedPassword encrypted password text to persist
     * @return ordered file values
     */
    public Map<String, String> toFileValues(final String encryptedPassword) {
        final Map<String, String> values = new LinkedHashMap<>();
        values.put("application.version", applicationVersion);
        values.put("application.use-database-properties", Boolean.toString(useDatabaseProperties));
        values.put("database.url", databaseUrl);
        values.put("database.user", databaseUser);
        values.put("database.password", encryptedPassword);
        return values;
    }

    /**
     * Returns one updated instance with a new password.
     *
     * @param password resolved plain-text password
     * @return updated bootstrap properties
     */
    public ApplicationBootstrapProperties withDatabasePassword(final String password) {
        return new ApplicationBootstrapProperties(
                applicationVersion,
                databaseUrl,
                databaseUser,
                Objects.requireNonNull(password, "password"),
                useDatabaseProperties);
    }

    /**
     * Returns one updated instance with a new database-backed flag.
     *
     * @param enabled flag value
     * @return updated bootstrap properties
     */
    public ApplicationBootstrapProperties withUseDatabaseProperties(final boolean enabled) {
        return new ApplicationBootstrapProperties(
                applicationVersion,
                databaseUrl,
                databaseUser,
                databasePassword,
                enabled);
    }

    /**
     * Returns one updated instance with merged plain-text values.
     *
     * @param values bootstrap override values
     * @return merged bootstrap properties
     */
    public ApplicationBootstrapProperties merge(final Map<String, String> values) {
        final var properties = new ApplicationPropertyValues(values);
        return new ApplicationBootstrapProperties(
                properties.text("version", applicationVersion),
                properties.text("database.url", databaseUrl),
                properties.text("database.user", databaseUser),
                properties.text("database.password", databasePassword),
                properties.bool("use-database-properties", useDatabaseProperties));
    }

    /**
     * Requires one non-blank text field.
     *
     * @param value source value
     * @param fieldName field name
     * @return trimmed value
     */
    private static String requireText(final String value, final String fieldName) {
        try {
            return ValidationRules.requireText(value, fieldName);
        } catch (IllegalArgumentException exception) {
            throw new OpenDataConfigurationException(fieldName + " must not be blank.", exception);
        }
    }
}
