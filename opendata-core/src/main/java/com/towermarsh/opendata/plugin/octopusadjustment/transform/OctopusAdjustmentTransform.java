/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.transform;

import com.towermarsh.opendata.plugin.octopus.transform.OctopusStatementParser;
import com.towermarsh.opendata.plugin.octopus.transform.model.ElectricityRecord;
import com.towermarsh.opendata.plugin.octopus.transform.model.GasRecord;
import com.towermarsh.opendata.plugin.octopusadjustment.extract.ExtractedOctopusAdjustment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Parses adjustment PDFs using the existing public Octopus statement parser.
 *
 * <p>{@link OctopusStatementParser#parseAllFromFile(java.nio.file.Path)} reads
 * each PDF once, derives bill dates and bill periods from document content, and
 * returns the same validated electricity and gas records used by the ordinary
 * Octopus plugin.</p>
 *
 * @author Terry Curran
 * @version 3.1.0
 * @since 3.1.0
 */
public final class OctopusAdjustmentTransform {

    private static final Logger LOGGER = Logger.getLogger(OctopusAdjustmentTransform.class.getName());

    /**
     * Transforms all extracted adjustment PDFs.
     *
     * @param sources source adjustment PDFs
     * @return parsed adjustment batch
     * @throws IOException if a PDF cannot be read
     * @since 3.1.0
     */
    public OctopusAdjustmentParseResult transform(
            final List<ExtractedOctopusAdjustment> sources) throws IOException {
        Objects.requireNonNull(sources, "sources");

        final List<ElectricityRecord> electricity = new ArrayList<>();
        final List<GasRecord> gas = new ArrayList<>();
        for (var source : sources) {
            Objects.requireNonNull(source, "source");
            final var parsed = OctopusStatementParser.parseAllFromFile(source.path());
            electricity.addAll(parsed.electricityRecords());
            gas.addAll(parsed.gasRecords());
        }

        LOGGER.info(() -> "Octopus adjustment transform: files=%d, electricity=%d, gas=%d"
                .formatted(sources.size(), electricity.size(), gas.size()));
        return new OctopusAdjustmentParseResult(electricity, gas, sources);
    }
}
