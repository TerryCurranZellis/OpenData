/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.finalise;

import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.octopus.extract.ExtractedOctopusStatement;
import com.towermarsh.opendata.plugin.octopus.initialise.OctopusConfiguration;
import com.towermarsh.opendata.plugin.octopus.transform.OctopusParseResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 
 * Archives successfully loaded source statements and reports batch statistics. 
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class OctopusFinalise {
    private static final Logger LOGGER = Logger.getLogger(OctopusFinalise.class.getName());

    public void finalise(
            final OctopusConfiguration configuration,
            final List<ExtractedOctopusStatement> statements,
            final OctopusParseResult parseResult,
            final PluginMetrics metrics,
            final boolean dryRun,
            final boolean completed) {
        Objects.requireNonNull(configuration, "configuration");
        Objects.requireNonNull(statements, "statements");
        Objects.requireNonNull(parseResult, "parseResult");
        Objects.requireNonNull(metrics, "metrics");
        LOGGER.info(() -> "Octopus finalise: files=%d, electricity=%d, gas=%d, inserted=%d, updated=%d, skipped=%d"
                .formatted(statements.size(), parseResult.electricityRecords().size(),
                        parseResult.gasRecords().size(), metrics.inserted(), metrics.updated(), metrics.skipped()));
        if (dryRun || !completed || statements.isEmpty()) return;
        try {
            Files.createDirectories(configuration.archiveDirectory());
            for (var statement : statements) {
                final var target = configuration.archiveDirectory().resolve(statement.fileName());
                Files.move(statement.path(), target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            LOGGER.log(Level.WARNING, "Octopus records were committed but one or more source files could not be archived", exception);
        }
    }
}
