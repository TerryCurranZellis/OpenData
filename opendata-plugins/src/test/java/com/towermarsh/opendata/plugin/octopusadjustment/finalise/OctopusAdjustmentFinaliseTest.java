/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.finalise;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.octopusadjustment.extract.ExtractedOctopusAdjustment;
import com.towermarsh.opendata.plugin.octopusadjustment.initialise.OctopusAdjustmentConfiguration;
import com.towermarsh.opendata.plugin.octopusadjustment.transform.OctopusAdjustmentParseResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests adjustment finalise dry-run archive isolation. */
class OctopusAdjustmentFinaliseTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void dryRunDoesNotMoveSourceFile() throws Exception {
        final var sourcePath = temporaryDirectory.resolve("A-5F191685-419015087-1.pdf");
        Files.writeString(sourcePath, "source");
        final var source = new ExtractedOctopusAdjustment(
                sourcePath,
                sourcePath.getFileName().toString(),
                "0".repeat(64),
                Files.size(sourcePath));
        final var archive = temporaryDirectory.resolve("archive");
        final var configuration = new OctopusAdjustmentConfiguration(
                "A-5F191685",
                temporaryDirectory,
                temporaryDirectory.resolve("work"),
                archive);
        final var result = new OctopusAdjustmentParseResult(
                List.of(), List.of(), List.of(source));

        new OctopusAdjustmentFinalise().finalise(
                configuration,
                List.of(source),
                result,
                PluginMetrics.ZERO,
                true,
                true);

        assertTrue(Files.exists(sourcePath));
        assertFalse(Files.exists(archive.resolve(source.fileName())));
    }
}
