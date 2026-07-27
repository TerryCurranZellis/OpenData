/*
 * Filename: DatabasePoolConfiguration.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import java.time.Duration;
import java.util.Objects;

/** SQL Server and Apache DBCP pool settings.
 * @param driverClass JDBC driver class name
 * @param jdbcUrl JDBC connection URL
 * @param user database login name
 * @param password database password or blank when supplied externally later
 * @param poolName Apache DBCP pool registration name
 * @param maxTotal maximum number of pooled connections
 * @param maxIdle maximum number of idle pooled connections
 * @param minIdle minimum number of idle pooled connections
 * @param maxWait maximum time to wait for a pooled connection
 * @param validationQuery SQL query used to validate pooled connections
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public record DatabasePoolConfiguration(
        String driverClass,
        String jdbcUrl,
        String user,
        String password,
        String poolName,
        int maxTotal,
        int maxIdle,
        int minIdle,
        Duration maxWait,
        String validationQuery) {

    /** Validates and normalises record components. */
    public DatabasePoolConfiguration {
        driverClass = requireText(driverClass, "driverClass");
        jdbcUrl = requireText(jdbcUrl, "jdbcUrl");
        user = requireText(user, "user");
        Objects.requireNonNull(password, "password");
        poolName = requireText(poolName, "poolName");
        Objects.requireNonNull(maxWait, "maxWait");
        validationQuery = requireText(validationQuery, "validationQuery");
        if (maxTotal < 1 || maxIdle < 0 || minIdle < 0 || maxIdle > maxTotal || minIdle > maxIdle) {
            throw new OpenDataConfigurationException("Invalid database pool size configuration.");
        }
        if (maxWait.isNegative() || maxWait.isZero()) {
            throw new OpenDataConfigurationException("database.pool.max-wait-seconds must be positive.");
        }
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
        if (value.isBlank()) {
            throw new OpenDataConfigurationException(name + " must not be blank.");
        }
        return value.trim();
    }
}
