/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.finalise;

import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.octopusadjustment.extract.ExtractedOctopusAdjustment;
import com.towermarsh.opendata.plugin.octopusadjustment.initialise.OctopusAdjustmentConfiguration;
import com.towermarsh.opendata.plugin.octopusadjustment.transform.OctopusAdjustmentParseResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reports adjustment results and archives committed source PDFs.
 *
 * <p>Archiving occurs only after a successful write-mode load. Archive failure
 * is reported as a warning and does not misrepresent an already committed SQL
 * transaction as rolled back.</p>
 *
 * @author Terry Curran
 * @version 3.1.0
 * @since 3.1.0
 */
public final class OctopusAdjustmentFinalise {

    private static final Logger LOGGER = Logger.getLogger(OctopusAdjustmentFinalise.class.getName());

    /**
     * Finalises one adjustment run.
     *
     * @param configuration typed configuration
     * @param sources extracted sources
     * @param parseResult transformed result
     * @param metrics load metrics
     * @param dryRun whether the run is a dry run
     * @param completed whether load completed successfully
     * @since 3.1.0
     */
    public void finalise(
            final OctopusAdjustmentConfiguration configuration,
            final List<ExtractedOctopusAdjustment> sources,
            final OctopusAdjustmentParseResult parseResult,
            final PluginMetrics metrics,
            final boolean dryRun,
            final boolean completed) {
        Objects.requireNonNull(configuration, "configuration");
        Objects.requireNonNull(sources, "sources");
        Objects.requireNonNull(parseResult, "parseResult");
        Objects.requireNonNull(metrics, "metrics");

        LOGGER.info(() -> "Octopus adjustment finalise: files=%d, electricity=%d, gas=%d, inserted=%d, updated=%d, skipped=%d"
                .formatted(
                        sources.size(),
                        parseResult.electricityRecords().size(),
                        parseResult.gasRecords().size(),
                        metrics.inserted(),
                        metrics.updated(),
                        metrics.skipped()));

        if (dryRun || !completed || sources.isEmpty()) {
            return;
        }

        try {
            Files.createDirectories(configuration.archiveDirectory());
            for (var source : sources) {
                final var target = configuration.archiveDirectory().resolve(source.fileName());
                Files.move(source.path(), target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            LOGGER.log(
                    Level.WARNING,
                    "Octopus adjustment records were committed but one or more source files could not be archived",
                    exception);
        }
    }
}
