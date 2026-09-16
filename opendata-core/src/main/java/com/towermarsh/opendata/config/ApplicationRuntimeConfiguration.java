/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Application-level settings loaded before plugin execution.
 *
 * @param database database connection and pool settings
 * @param execution plugin execution settings
 * @param logging logging settings
  *
 * @author Terry Curran
 * @version 2.1
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
     * Loads packaged runtime configuration and overlays caller-supplied values.
     *
     * @param overrides application-level override values without the `application.` prefix
     * @return resolved runtime configuration
     */
    public static ApplicationRuntimeConfiguration load(final Map<String, String> overrides) {
        return load(new ClasspathConfigurationPropertiesSource(), overrides);
    }

    /**
     * Loads runtime configuration from one property source and overlays any
     * caller-supplied application values.
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
        final var properties = new ApplicationPropertyValues(values);
        final var database = new DatabasePoolConfiguration(
                properties.text("database.driver-class", DEFAULT_DRIVER_CLASS),
                properties.requiredText("database.url"),
                properties.requiredText("database.user"),
                PASSWORD_CIPHER.decrypt(properties.text("database.password", "")),
                properties.text("database.pool.name", DEFAULT_POOL_NAME),
                properties.integer("database.pool.max-total", 8),
                properties.integer("database.pool.max-idle", 8),
                properties.integer("database.pool.min-idle", 1),
                Duration.ofSeconds(properties.integer("database.pool.max-wait-seconds", 30)),
                properties.text("database.pool.validation-query", "SELECT 1"));
        final var execution = new ExecutionConfiguration(
                properties.integer("execution.max-parallel-plugins", 4),
                Duration.ofSeconds(properties.integer("execution.shutdown-timeout-seconds", 30)));
        final var logging = new LoggingConfiguration(
                Path.of(properties.text("logging.directory", "logs")),
                properties.integer("logging.file-limit-bytes", 10_485_760),
                properties.integer("logging.file-count", 10),
                properties.bool("logging.append", true));
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

}
