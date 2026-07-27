/*
 * Filename: SQLServerResourceTest.java
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.towermarsh.opendata.config.DatabasePoolConfiguration;
import java.time.Duration;
import org.junit.jupiter.api.Test;

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
