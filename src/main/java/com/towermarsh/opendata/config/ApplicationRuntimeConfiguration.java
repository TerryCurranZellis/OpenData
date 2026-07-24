/*
 * Filename: ApplicationRuntimeConfiguration.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/** Application-level settings loaded before plugin execution. */
public record ApplicationRuntimeConfiguration(
        DatabasePoolConfiguration database,
        ExecutionConfiguration execution,
        LoggingConfiguration logging) {

    private static final String RESOURCE = "config/application.properties";

    public ApplicationRuntimeConfiguration {
        Objects.requireNonNull(database, "database");
        Objects.requireNonNull(execution, "execution");
        Objects.requireNonNull(logging, "logging");
    }

    public static ApplicationRuntimeConfiguration load(final Map<String, String> overrides) {
        final Map<String, String> values = loadResource();
        values.putAll(Objects.requireNonNull(overrides, "overrides"));
        final var database = new DatabasePoolConfiguration(
                required(values, "database.driver-class"),
                required(values, "database.url"),
                required(values, "database.user"),
                values.getOrDefault("database.password", ""),
                required(values, "database.pool.name"),
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

    private static Map<String, String> loadResource() {
        final var classLoader = Thread.currentThread().getContextClassLoader();
        try (var input = classLoader.getResourceAsStream(RESOURCE)) {
            if (input == null) {
                throw new OpenDataConfigurationException("Application resource not found: " + RESOURCE);
            }
            final var properties = new Properties();
            properties.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            final Map<String, String> result = new LinkedHashMap<>();
            properties.stringPropertyNames().forEach(name -> result.put(normalise(name), properties.getProperty(name).trim()));
            return result;
        } catch (IOException exception) {
            throw new OpenDataConfigurationException("Unable to read " + RESOURCE, exception);
        }
    }

    private static String required(final Map<String, String> values, final String key) {
        final String value = values.get(normalise(key));
        if (value == null || value.isBlank()) {
            throw new OpenDataConfigurationException("Required application property is missing: " + key);
        }
        return value.trim();
    }

    private static int integer(final Map<String, String> values, final String key, final int defaultValue) {
        final String value = values.get(normalise(key));
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new OpenDataConfigurationException("Application property must be an integer: " + key, exception);
        }
    }

    private static boolean bool(final Map<String, String> values, final String key, final boolean defaultValue) {
        final String value = values.get(normalise(key));
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return switch (value.trim().toLowerCase(java.util.Locale.ROOT)) {
            case "true", "yes", "1", "on" -> true;
            case "false", "no", "0", "off" -> false;
            default -> throw new OpenDataConfigurationException("Application property must be a boolean: " + key);
        };
    }

    private static String normalise(final String value) {
        return value.trim().toLowerCase(java.util.Locale.ROOT);
    }
}
