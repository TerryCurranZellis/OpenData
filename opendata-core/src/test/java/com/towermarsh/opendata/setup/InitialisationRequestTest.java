/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.setup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/**
 * Validates first-run request input and safe SQL Server JDBC construction.
 *
 * @author Terry Curran
 * @version 4.0.0
 */
class InitialisationRequestTest {

    /** Ensures the application URL targets the selected database only. */
    @Test
    void buildsValidatedJdbcUrl() {
        final var request = request("testserver", "OpenData");
        assertEquals("jdbc:sqlserver://testserver:1433;databaseName=OpenData;encrypt=true;"
                + "trustServerCertificate=false;loginTimeout=15", request.jdbcUrl(request.database()));
    }

    /** Rejects SQL identifiers and JDBC host strings that could alter SQL. */
    @Test
    void rejectsUnsafeIdentifiersAndHostValues() {
        assertThrows(IllegalArgumentException.class, () -> request("localhost;encrypt=false", "OpenData"));
        assertThrows(IllegalArgumentException.class, () -> request("localhost", "OpenData];DROP"));
    }

    /** Rejects invalid port and empty secret values. */
    @Test
    void rejectsInvalidPortAndMissingPassword() {
        assertThrows(IllegalArgumentException.class,
                () -> new InitialisationRequest("localhost", 0, "OpenData", "sa", "admin",
                        "OpenData", "application", Path.of("key"), false));
        assertThrows(IllegalArgumentException.class,
                () -> new InitialisationRequest("localhost", 1433, "OpenData", "sa", "",
                        "OpenData", "application", Path.of("key"), false));
    }

    /** Creates a representative valid request. */
    private static InitialisationRequest request(final String server, final String database) {
        return new InitialisationRequest(server, 1433, database, "sa", "admin",
                "OpenData", "application", Path.of("config/keys/key"), false);
    }
}
