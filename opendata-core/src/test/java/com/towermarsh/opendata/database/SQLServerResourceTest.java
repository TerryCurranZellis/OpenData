/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.core.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.towermarsh.opendata.config.DatabasePoolConfiguration;
import java.time.Duration;
import org.junit.jupiter.api.Test;

/**
 * Tests SQL Server connection-pool creation, closure and singleton lifetime.
 *
 * @author Terry Curran
 * @since 1.0.0
 * @version 4.1.0
 */
class SQLServerResourceTest {

    @Test
    void exposesConfiguredPoolLimitWithoutOpeningAConnection() {
        DatabasePoolConfiguration configuration = new DatabasePoolConfiguration(
                "com.microsoft.sqlserver.jdbc.SQLServerDriver",
                "jdbc:sqlserver://localhost;databaseName=OpenData;encrypt=true;trustServerCertificate=true",
                "OpenData",
                "local-test-password",
                "OpenDataTestPool",
                9,
                3,
                0,
                Duration.ofSeconds(10),
                "SELECT 1");

        SQLServerResource resource = SQLServerResource.initialise(configuration);
        assertFalse(resource.getPoolSnapshot().closed());
        assertEquals(9, resource.getPoolSnapshot().maximumConnections());

        resource.close();
        assertTrue(resource.getPoolSnapshot().closed());
    }

    /**
     * Closing a pool clears the singleton; a subsequent initialisation creates
     * a distinct, live resource. Closing the stale reference has no effect.
     */
    @Test
    void canReinitialiseAfterShutdownWithoutClosingNewPool() {
        final var configuration = new DatabasePoolConfiguration(
                "com.microsoft.sqlserver.jdbc.SQLServerDriver",
                "jdbc:sqlserver://localhost;databaseName=OpenData;encrypt=true;trustServerCertificate=true",
                "OpenData",
                "local-test-password",
                "OpenDataReinitialisationTestPool",
                2,
                0,
                0,
                Duration.ofSeconds(2),
                "SELECT 1");

        final var first = SQLServerResource.initialise(configuration);
        try {
            assertSame(first, SQLServerResource.getInstance());
            first.close();
            assertThrows(IllegalStateException.class, SQLServerResource::getInstance);
            final var second = SQLServerResource.initialise(configuration);
            try {
                assertNotSame(first, second);
                assertSame(second, SQLServerResource.getInstance());
                first.close();
                assertSame(second, SQLServerResource.getInstance());
                assertFalse(second.getPoolSnapshot().closed());
            } finally {
                second.close();
            }
            assertThrows(IllegalStateException.class, SQLServerResource::getInstance);
        } finally {
            first.close();
        }
    }

}
