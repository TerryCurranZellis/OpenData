/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.extract;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.database.UnavailableDatabaseResourceManager;
import com.towermarsh.opendata.plugin.PluginDescriptor;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.octopus.initialise.OctopusConfiguration;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests Octopus statement discovery and dry-run database isolation. */
class OctopusExtractTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void parsesStatementDateFromExpectedFilename() {
        assertEquals(LocalDate.of(2026, 7, 31),
                OctopusExtract.statementDate(
                        Path.of("octopus-energy-statement-2026-07-31.pdf")));
    }

    @Test
    void rejectsOtherPdfNames() {
        assertThrows(IllegalArgumentException.class,
                () -> OctopusExtract.statementDate(
                        Path.of("statement-2026-07-31.pdf")));
    }

    @Test
    void dryRunDoesNotQueryProcessedFileLedger() throws Exception {
        final var context = new PluginExecutionContext(
                UUID.randomUUID(),
                new PluginDescriptor(
                        "octopus", "Octopus", "", "example.OctopusPlugin", true, 1),
                mock(PluginDefinition.class),
                new UnavailableDatabaseResourceManager(),
                Clock.systemUTC(),
                true);
        final var configuration = new OctopusConfiguration(
                temporaryDirectory,
                temporaryDirectory.resolve("work"),
                temporaryDirectory.resolve("archive"));

        assertEquals(0, new OctopusExtract().extract(configuration, context).size());
    }
}
