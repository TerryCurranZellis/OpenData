/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.util.Properties;
import org.junit.jupiter.api.Test;

/**
 * @author Terry Curran
 * @version 1.0.0
 */
class DatabasePoolConfigTest {

    @Test
    void readsOverridesFromProperties() {
        Properties properties = new Properties();
        properties.setProperty("database.pool.max-total", "20");
        properties.setProperty("database.pool.max-wait-millis", "45000");
        properties.setProperty("database.pool.test-on-borrow", "false");

        DatabasePoolConfig config = DatabasePoolConfig.from(properties);

        assertEquals(20, config.maxTotal());
        assertEquals(Duration.ofSeconds(45), config.maxWait());
        assertEquals(false, config.testOnBorrow());
    }

    @Test
    void rejectsIdleLimitAboveTotalLimit() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new DatabasePoolConfig(
                        1,
                        1,
                        10,
                        5,
                        Duration.ofSeconds(10),
                        Duration.ofMinutes(1),
                        true,
                        "SELECT 1",
                        5));
    }
}
