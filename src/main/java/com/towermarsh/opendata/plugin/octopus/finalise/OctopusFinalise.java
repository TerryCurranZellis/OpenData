/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.finalise;

import com.towermarsh.opendata.exception.PluginException;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.octopus.initialise.OctopusConfiguration;
import com.towermarsh.opendata.plugin.octopus.transform.OctopusParseResult;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Finalise step for the Octopus plugin.
 *
 * <p><b>Placeholder implementation.</b> In a full implementation this step
 * will be responsible for:
 * <ol>
 *   <li>Moving processed PDF files from the input directory to the archive
 *       directory (on a successful write run).</li>
 *   <li>Deleting any temporary files written to the working directory.</li>
 *   <li>Logging final run statistics (records read, inserted, updated,
 *       skipped).</li>
 *   <li>Returning control to the plugin so it can report the final
 *       {@link PluginMetrics} to the framework.</li>
 * </ol>
 *
 * <p>The current placeholder implementation logs the statistics and returns
 * without modifying the file system.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public final class OctopusFinalise {

    private static final Logger LOGGER = Logger.getLogger(OctopusFinalise.class.getName());

    /**
     * Performs post-run cleanup and reports final statistics.
     *
     * @param configuration Octopus plugin configuration
     * @param pdfFiles      list of PDF files that were processed by the extract step
     * @param parseResult   records produced by the transform step
     * @param metrics       load outcome metrics
     * @throws PluginException if cleanup fails (e.g. archiving files)
     */
    public void finalise(
            final OctopusConfiguration configuration,
            final List<Path> pdfFiles,
            final OctopusParseResult parseResult,
            final PluginMetrics metrics) throws PluginException {
        Objects.requireNonNull(configuration, "configuration");
        Objects.requireNonNull(pdfFiles, "pdfFiles");
        Objects.requireNonNull(parseResult, "parseResult");
        Objects.requireNonNull(metrics, "metrics");

        // Log run statistics
        LOGGER.info(() -> "Octopus finalise: run complete"
                + " | PDFs processed: " + pdfFiles.size()
                + " | electricity records: " + parseResult.electricityRecords().size()
                + " | gas records: " + parseResult.gasRecords().size()
                + " | inserted: " + metrics.inserted()
                + " | updated: " + metrics.updated()
                + " | skipped: " + metrics.skipped());

        // TODO: Implement file archiving and working-directory cleanup.
        // Steps:
        //   1. For each PDF in pdfFiles, move it to configuration.archiveDirectory()
        //   2. Delete any temporary files from configuration.workingDirectory()
        LOGGER.fine("Octopus finalise: file archiving not yet implemented");
    }
}
