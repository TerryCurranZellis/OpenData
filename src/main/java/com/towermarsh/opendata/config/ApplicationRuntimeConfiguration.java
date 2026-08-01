/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Application-level settings loaded before plugin execution.
 *
 * @param database database connection and pool settings
 * @param execution plugin execution settings
 * @param logging logging settings
  *
 * @author Terry Curran
 * @version 1.0.0
 */
public record ApplicationRuntimeConfiguration(
        DatabasePoolConfiguration database,
        ExecutionConfiguration execution,
        LoggingConfiguration logging) {

    private static final String DEFAULT_DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String DEFAULT_POOL_NAME = "OpenData";
    private static final ConfigurationPasswordCipher PASSWORD_CIPHER
            = new RsaConfigurationPasswordCipher();

    /** 
     * Validates and normalises record components. 
     */
    public ApplicationRuntimeConfiguration {
        Objects.requireNonNull(database, "database");
        Objects.requireNonNull(execution, "execution");
        Objects.requireNonNull(logging, "logging");
    }

    /**
     * Loads packaged runtime configuration and overlays application overrides.
     *
     * @param overrides application-level override values without the `application.` prefix
     * @return resolved runtime configuration
     */
    public static ApplicationRuntimeConfiguration load(final Map<String, String> overrides) {
        return load(new ClasspathConfigurationPropertiesSource(), overrides);
    }

    /**
     * Loads runtime configuration from one property source and overlays any
     * invocation overrides.
     *
     * @param source configuration property source
     * @param overrides application-level override values without the
     * {@code application.} prefix
     * @return resolved runtime configuration
     */
    public static ApplicationRuntimeConfiguration load(
            final ConfigurationPropertiesSource source,
            final Map<String, String> overrides) {
        final var values = defaultPropertyValues();
        values.putAll(Objects.requireNonNull(source, "source").loadApplicationProperties());
        values.putAll(Objects.requireNonNull(overrides, "overrides"));
        final var database = new DatabasePoolConfiguration(
                values.getOrDefault("database.driver-class", DEFAULT_DRIVER_CLASS),
                required(values, "database.url"),
                required(values, "database.user"),
                PASSWORD_CIPHER.decrypt(values.getOrDefault("database.password", "")),
                values.getOrDefault("database.pool.name", DEFAULT_POOL_NAME),
                integer(values, "database.pool.max-total", 8),
                integer(values, "database.pool.max-idle", 8),
                integer(values, "database.pool.min-idle", 1),
                Duration.ofSeconds(integer(values, "database.pool.max-wait-seconds", 30)),
                values.getOrDefault("database.pool.validation-query", "SELECT 1"));
        final var execution = new ExecutionConfiguration(
                integer(values, "execution.max-parallel-plugins", 4),
                Duration.ofSeconds(integer(values, "execution.shutdown-timeout-seconds", 30)));
        final var logging = new LoggingConfiguration(
                Path.of(values.getOrDefault("logging.directory", "logs")),
                integer(values, "logging.file-limit-bytes", 10_485_760),
                integer(values, "logging.file-count", 10),
                bool(values, "logging.append", true));
        return new ApplicationRuntimeConfiguration(database, execution, logging);
    }

    /**
     * Returns built-in runtime defaults used when the bootstrap file only holds
     * database access details.
     *
     * @return default runtime properties
     */
    public static Map<String, String> defaultPropertyValues() {
        final Map<String, String> result = new LinkedHashMap<>();
        result.put("database.driver-class", DEFAULT_DRIVER_CLASS);
        result.put("database.pool.name", DEFAULT_POOL_NAME);
        result.put("database.pool.max-total", "8");
        result.put("database.pool.max-idle", "8");
        result.put("database.pool.min-idle", "1");
        result.put("database.pool.max-wait-seconds", "30");
        result.put("database.pool.validation-query", "SELECT 1");
        result.put("execution.max-parallel-plugins", "4");
        result.put("execution.shutdown-timeout-seconds", "30");
        result.put("logging.directory", "logs");
        result.put("logging.file-limit-bytes", "10485760");
        result.put("logging.file-count", "10");
        result.put("logging.append", "true");
        return result;
    }

    /**
     * Returns a required application property.
     *
     * @param values property values indexed by normalised key
     * @param key property key to resolve
     * @return trimmed property value
     */
    private static String required(final Map<String, String> values, final String key) {
        final var value = values.get(normalise(key));
        if (value == null || value.isBlank()) {
            throw new OpenDataConfigurationException("Required application property is missing: " + key);
        }
        return value.trim();
    }

    /**
     * Returns an integer application property or a default value.
     *
     * @param values property values indexed by normalised key
     * @param key property key to resolve
     * @param defaultValue fallback value when the property is absent
     * @return parsed integer value
     */
    private static int integer(final Map<String, String> values, final String key, final int defaultValue) {
        final var value = values.get(normalise(key));
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new OpenDataConfigurationException("Application property must be an integer: " + key, exception);
        }
    }

    /**
     * Returns a Boolean application property or a default value.
     *
     * @param values property values indexed by normalised key
     * @param key property key to resolve
     * @param defaultValue fallback value when the property is absent
     * @return parsed Boolean value
     */
    private static boolean bool(final Map<String, String> values, final String key, final boolean defaultValue) {
        final var value = values.get(normalise(key));
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "true", "yes", "1", "on" -> true;
            case "false", "no", "0", "off" -> false;
            default -> throw new OpenDataConfigurationException("Application property must be a boolean: " + key);
        };
    }

    /**
     * Normalises a property name for case-insensitive look-ups.
     *
     * @param value property name to normalise
     * @return trimmed lower-case property name
     */
    private static String normalise(final String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
