/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.transform;

import com.towermarsh.opendata.plugin.octopus.extract.ExtractedOctopusStatement;
import com.towermarsh.opendata.plugin.octopus.transform.model.ElectricityRecord;
import com.towermarsh.opendata.plugin.octopus.transform.model.GasRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/** Transforms a complete extraction batch without rereading its PDF files. */
public final class OctopusTransform {
    private static final Logger LOGGER = Logger.getLogger(OctopusTransform.class.getName());

    /**
     *
     * @param statements
     * @return
     */
    public OctopusParseResult transform(final List<ExtractedOctopusStatement> statements) {
        Objects.requireNonNull(statements, "statements");
        final List<ElectricityRecord> electricity = new ArrayList<>();
        final List<GasRecord> gas = new ArrayList<>();
        for (ExtractedOctopusStatement statement : statements) {
            final OctopusParseResult result = OctopusStatementParser.parseExtracted(
                    statement.text(), statement.fileName(), statement.statementDate());
            electricity.addAll(result.electricityRecords());
            gas.addAll(result.gasRecords());
        }
        LOGGER.info(() -> "Octopus transform: %d file(s), %d electricity record(s), %d gas record(s)"
                .formatted(statements.size(), electricity.size(), gas.size()));
        return new OctopusParseResult(electricity, gas, statements);
    }
}
