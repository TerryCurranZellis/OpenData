/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

import java.time.Duration;
import java.util.Objects;
import java.util.Properties;

/**
 * Immutable Apache DBCP connection-pool settings.
 * @param initialSize initial number of pooled connections
 * @param minIdle minimum number of idle pooled connections
 * @param maxIdle maximum number of idle pooled connections
 * @param maxTotal maximum number of pooled connections
 * @param maxWait maximum time to wait for a connection
 * @param minEvictableIdleTime minimum idle time before eviction is permitted
 * @param testOnBorrow whether connections are validated when borrowed
 * @param validationQuery SQL validation query
 * @param validationQueryTimeoutSeconds timeout for the validation query in seconds
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public record DatabasePoolConfig(
        int initialSize,
        int minIdle,
        int maxIdle,
        int maxTotal,
        Duration maxWait,
        Duration minEvictableIdleTime,
        boolean testOnBorrow,
        String validationQuery,
        int validationQueryTimeoutSeconds) {

    private static final String PREFIX = "database.pool.";

    /** Validates and normalises record components. */
    public DatabasePoolConfig {
        Objects.requireNonNull(maxWait, "maxWait");
        Objects.requireNonNull(minEvictableIdleTime, "minEvictableIdleTime");
        validationQuery = Objects.requireNonNull(validationQuery, "validationQuery").trim();

        if (initialSize < 0) {
            throw new IllegalArgumentException("initialSize cannot be negative");
        }
        if (minIdle < 0) {
            throw new IllegalArgumentException("minIdle cannot be negative");
        }
        if (maxIdle < 1) {
            throw new IllegalArgumentException("maxIdle must be at least one");
        }
        if (maxTotal < 1) {
            throw new IllegalArgumentException("maxTotal must be at least one");
        }
        if (initialSize > maxTotal) {
            throw new IllegalArgumentException("initialSize cannot exceed maxTotal");
        }
        if (minIdle > maxIdle) {
            throw new IllegalArgumentException("minIdle cannot exceed maxIdle");
        }
        if (maxIdle > maxTotal) {
            throw new IllegalArgumentException("maxIdle cannot exceed maxTotal");
        }
        if (maxWait.isNegative() || maxWait.isZero()) {
            throw new IllegalArgumentException("maxWait must be positive");
        }
        if (minEvictableIdleTime.isNegative()) {
            throw new IllegalArgumentException("minEvictableIdleTime cannot be negative");
        }
        if (validationQuery.isBlank()) {
            throw new IllegalArgumentException("validationQuery cannot be blank");
        }
        if (validationQueryTimeoutSeconds < 1) {
            throw new IllegalArgumentException(
                    "validationQueryTimeoutSeconds must be at least one");
        }
    }

    /**
     * Conservative defaults for the initial command-line application. They can
     * support concurrent plugin work later without opening excessive sessions.
     *
     * @return default settings
     */
    public static DatabasePoolConfig defaults() {
        return new DatabasePoolConfig(
                1,
                1,
                4,
                12,
                Duration.ofSeconds(30),
                Duration.ofMinutes(5),
                true,
                "SELECT 1",
                5);
    }

    /**
     * Builds settings from standard Java properties, falling back to defaults.
     *
     * @param properties properties to read
     * @return parsed pool settings
     */
    public static DatabasePoolConfig from(Properties properties) {
        Objects.requireNonNull(properties, "properties");
        DatabasePoolConfig defaults = defaults();
        return new DatabasePoolConfig(
                integer(properties, "initial-size", defaults.initialSize()),
                integer(properties, "min-idle", defaults.minIdle()),
                integer(properties, "max-idle", defaults.maxIdle()),
                integer(properties, "max-total", defaults.maxTotal()),
                Duration.ofMillis(longValue(
                        properties, "max-wait-millis", defaults.maxWait().toMillis())),
                Duration.ofMillis(longValue(
                        properties,
                        "min-evictable-idle-millis",
                        defaults.minEvictableIdleTime().toMillis())),
                booleanValue(properties, "test-on-borrow", defaults.testOnBorrow()),
                properties.getProperty(
                        PREFIX + "validation-query", defaults.validationQuery()),
                integer(
                        properties,
                        "validation-query-timeout-seconds",
                        defaults.validationQueryTimeoutSeconds()));
    }

    /**
     * Reads an integer pool property.
     *
     * @param properties source properties
     * @param name property suffix after `database.pool.`
     * @param defaultValue fallback value
     * @return parsed integer value
     */
    private static int integer(Properties properties, String name, int defaultValue) {
        return Integer.parseInt(properties.getProperty(PREFIX + name, String.valueOf(defaultValue)).trim());
    }

    /**
     * Reads a long-valued pool property.
     *
     * @param properties source properties
     * @param name property suffix after `database.pool.`
     * @param defaultValue fallback value
     * @return parsed long value
     */
    private static long longValue(Properties properties, String name, long defaultValue) {
        return Long.parseLong(properties.getProperty(PREFIX + name, String.valueOf(defaultValue)).trim());
    }

    /**
     * Reads a boolean pool property.
     *
     * @param properties source properties
     * @param name property suffix after `database.pool.`
     * @param defaultValue fallback value
     * @return parsed boolean value
     */
    private static boolean booleanValue(
            Properties properties,
            String name,
            boolean defaultValue) {
        return Boolean.parseBoolean(
                properties.getProperty(PREFIX + name, String.valueOf(defaultValue)).trim());
    }
}
