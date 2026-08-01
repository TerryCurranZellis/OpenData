/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Minimal bootstrap properties required before database-backed configuration can
 * be loaded.
 *
 * @param applicationVersion configured application version string
 * @param databaseUrl SQL Server JDBC URL
 * @param databaseUser SQL Server login name
 * @param databasePassword SQL Server password in plain text after decryption
 * @param useDatabaseProperties whether runtime properties are loaded from the database
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public record ApplicationBootstrapProperties(
        String applicationVersion,
        String databaseUrl,
        String databaseUser,
        String databasePassword,
        boolean useDatabaseProperties) {

    private static final String DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String POOL_NAME = "OpenData";
    private static final int MAX_TOTAL = 8;
    private static final int MAX_IDLE = 8;
    private static final int MIN_IDLE = 1;
    private static final int MAX_WAIT_SECONDS = 30;
    private static final String VALIDATION_QUERY = "SELECT 1";

    /**
     * Validates and normalises record components.
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
        final var applicationVersionValue = values.getOrDefault("version", applicationVersion);
        final var databaseUrlValue = values.getOrDefault("database.url", databaseUrl);
        final var databaseUserValue = values.getOrDefault("database.user", databaseUser);
        final var databasePasswordValue = values.getOrDefault("database.password", databasePassword);
        final var useDatabaseValue = values.getOrDefault(
                "use-database-properties",
                Boolean.toString(useDatabaseProperties));
        return new ApplicationBootstrapProperties(
                applicationVersionValue,
                databaseUrlValue,
                databaseUserValue,
                databasePasswordValue,
                parseBoolean(useDatabaseValue, "use-database-properties"));
    }

    /**
     * Parses a boolean bootstrap flag.
     *
     * @param value raw text
     * @param key property name
     * @return parsed boolean value
     */
    private static boolean parseBoolean(final String value, final String key) {
        return switch (Objects.requireNonNull(value, key).trim().toLowerCase()) {
            case "true", "yes", "1", "on" -> true;
            case "false", "no", "0", "off" -> false;
            default -> throw new OpenDataConfigurationException(
                    "Application property must be a boolean: " + key);
        };
    }

    /**
     * Requires one non-blank text field.
     *
     * @param value source value
     * @param fieldName field name
     * @return trimmed value
     */
    private static String requireText(final String value, final String fieldName) {
        Objects.requireNonNull(value, fieldName);
        final var result = value.trim();
        if (result.isBlank()) {
            throw new OpenDataConfigurationException(fieldName + " must not be blank.");
        }
        return result;
    }
}
