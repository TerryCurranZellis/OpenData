/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.towermarsh.opendata.config.DatabasePoolConfiguration;
import java.time.Duration;
import org.junit.jupiter.api.Test;

/**
 * @author Terry Curran
 * @version 1.0.0
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
}
