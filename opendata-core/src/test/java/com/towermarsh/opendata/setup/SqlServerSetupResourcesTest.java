/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.setup;

import com.towermarsh.opendata.database.SqlBatchParser;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Verifies that first-run SQL Server DDL is packaged as executable resources.
 *
 * @author Terry Curran
 * @since 4.1.0
 * @version 4.1.0
 */
class SqlServerSetupResourcesTest {

    private static final List<String> CORE_SCRIPTS = List.of(
            "sql/setup/002-create-core-schema.sql",
            "sql/setup/003-create-configuration-store.sql",
            "sql/setup/003a-create-plugin-registry.sql",
            "sql/setup/006-create-plugin-run-audit.sql");

    /**
     * Detects missing or empty setup resources before the first-run wizard
     * attempts to provision SQL Server.
     *
     * @throws IOException if an embedded setup resource cannot be read
     */
    @Test
    void includesExecutableSqlServerSetupScripts() throws IOException {
        for (var resource : CORE_SCRIPTS) {
            try (var stream = SqlServerInitialiser.class.getClassLoader()
                    .getResourceAsStream(resource)) {
                assertNotNull(stream, "Missing core setup resource: " + resource);
                var script = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                assertFalse(SqlBatchParser.batches(script).isEmpty(),
                        "Empty core setup script: " + resource);
            }
        }
    }
}
