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

/** SQL Server and Apache DBCP pool settings. */
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

    private static String requireText(final String value, final String name) {
        Objects.requireNonNull(value, name);
        if (value.isBlank()) {
            throw new OpenDataConfigurationException(name + " must not be blank.");
        }
        return value.trim();
    }
}
