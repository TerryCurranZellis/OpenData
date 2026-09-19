/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.extract;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.database.UnavailableDatabaseResourceManager;
import com.towermarsh.opendata.plugin.PluginDescriptor;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.octopusadjustment.initialise.OctopusAdjustmentConfiguration;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests adjustment filename discovery and dry-run database isolation. */
class OctopusAdjustmentExtractTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void matchesConfiguredAccountPrefixAndPdfExtension() {
        assertTrue(OctopusAdjustmentExtract.matchesCandidate(
                "A-5F191685-419015087-1.pdf", "A-5F191685"));
        assertTrue(OctopusAdjustmentExtract.matchesCandidate(
                "a-5f191685-123.PDF", "A-5F191685"));
        assertFalse(OctopusAdjustmentExtract.matchesCandidate(
                "A-OTHER-419015087-1.pdf", "A-5F191685"));
        assertFalse(OctopusAdjustmentExtract.matchesCandidate(
                "A-5F191685.pdf", "A-5F191685"));
    }

    @Test
    void dryRunDiscoversFilesWithoutQueryingDatabase() throws Exception {
        Files.writeString(temporaryDirectory.resolve("A-5F191685-419015087-1.pdf"), "first");
        Files.writeString(temporaryDirectory.resolve("A-5F191685-419015087-2.PDF"), "second");
        Files.writeString(temporaryDirectory.resolve("octopus-energy-statement-2026-08-01.pdf"), "ordinary");

        final var configuration = new OctopusAdjustmentConfiguration(
                "A-5F191685",
                temporaryDirectory,
                temporaryDirectory.resolve("work"),
                temporaryDirectory.resolve("archive"));
        final var context = new PluginExecutionContext(
                UUID.randomUUID(),
                new PluginDescriptor(
                        "octopus-adjustment",
                        "Octopus Energy Adjustments",
                        "",
                        "com.towermarsh.opendata.plugin.octopusadjustment.OctopusAdjustmentPlugin",
                        true,
                        1),
                mock(PluginDefinition.class),
                new UnavailableDatabaseResourceManager(),
                Clock.systemUTC(),
                true);

        final var sources = new OctopusAdjustmentExtract().extract(configuration, context);

        assertEquals(2, sources.size());
        assertEquals("A-5F191685-419015087-1.pdf", sources.get(0).fileName());
        assertEquals(64, sources.get(0).sha256().length());
    }
}
