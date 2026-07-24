package com.towermarsh.opendata.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.towermarsh.opendata.config.ApplicationConfig;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class SQLServerResourceTest {

    @Test
    void exposesConfiguredPoolLimitWithoutOpeningAConnection() throws Exception {
        ApplicationConfig applicationConfig = new ApplicationConfig(
                null,
                SQLServerResource.DEFAULT_JDBC_URL,
                SQLServerResource.DEFAULT_USER,
                "local-test-password");
        DatabasePoolConfig poolConfig = new DatabasePoolConfig(
                0,
                0,
                3,
                9,
                Duration.ofSeconds(10),
                Duration.ofMinutes(1),
                true,
                "SELECT 1",
                5);

        SQLServerResource resource = new SQLServerResource(
                applicationConfig,
                poolConfig);
        assertFalse(resource.isClosed());
        assertEquals(9, resource.getPoolSnapshot().maximumConnections());

        resource.close();
        assertTrue(resource.isClosed());
    }
}
