/*
 * Filename: DatabasePoolConfigTest.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The author may be contacted by email to the following address:
 *
 * terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.util.Properties;
import org.junit.jupiter.api.Test;

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
