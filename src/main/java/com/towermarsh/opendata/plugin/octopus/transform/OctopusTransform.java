/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.transform;

import com.towermarsh.opendata.exception.PluginException;
import com.towermarsh.opendata.plugin.octopus.initialise.OctopusConfiguration;
import com.towermarsh.opendata.plugin.octopus.transform.model.ElectricityRecord;
import com.towermarsh.opendata.plugin.octopus.transform.model.GasRecord;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Transform step for the Octopus plugin.
 *
 * <p>Parses Octopus Energy statement PDF files into structured
 * {@link ElectricityRecord} and {@link GasRecord} instances. This step
 * assumes that the PDF files are already present in the configured input
 * directory; the {@link com.towermarsh.opendata.plugin.octopus.extract.OctopusExtract}
 * step is responsible for making them available.
 *
 * <p>Parsing is delegated to {@link OctopusStatementParser}, which handles
 * the two-column PDF layout and all date/value extraction logic.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public final class OctopusTransform {

    private static final Logger LOGGER = Logger.getLogger(OctopusTransform.class.getName());

    /**
     * Transforms all Octopus PDF statement files in the configured input
     * directory into structured billing records.
     *
     * @param configuration Octopus plugin configuration
     * @return combined electricity and gas records; never {@code null}
     * @throws PluginException if the input directory cannot be read or any PDF
     *                         fails to parse
     */
    public OctopusParseResult transform(final OctopusConfiguration configuration)
            throws PluginException {
        Objects.requireNonNull(configuration, "configuration");

        final var parser = new OctopusStatementParser(configuration.inputDirectory());
        try {
            @SuppressWarnings("unchecked")
            final Object[] both = parser.parseBoth();
            final List<ElectricityRecord> electricityRecords = (List<ElectricityRecord>) both[0];
            final List<GasRecord> gasRecords = (List<GasRecord>) both[1];

            LOGGER.info(() -> "Octopus transform: %d electricity record(s), %d gas record(s)"
                    .formatted(electricityRecords.size(), gasRecords.size()));

            return new OctopusParseResult(electricityRecords, gasRecords);
        } catch (IOException e) {
            throw new PluginException("octopus", "Failed to parse PDF statement files in "
                    + configuration.inputDirectory(), e);
        }
    }
}
