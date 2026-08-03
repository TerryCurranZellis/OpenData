/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.transform;

import java.nio.file.Path;
import java.util.List;
import java.io.IOException;
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
     * @throws IOException if any downloaded PDF cannot be read
     */
    public OctopusParseResult transform(final List<Path> pdfFiles) throws IOException {
        Objects.requireNonNull(pdfFiles, "pdfFiles");
        final var result = OctopusStatementParser.parseAll(pdfFiles);
        LOGGER.info(() -> "Octopus transform: %d electricity record(s), %d gas record(s)"
                .formatted(result.electricityRecords().size(), result.gasRecords().size()));
        return result;
    }
}
