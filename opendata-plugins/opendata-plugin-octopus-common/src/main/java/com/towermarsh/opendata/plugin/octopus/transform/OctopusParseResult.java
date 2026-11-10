/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.transform;

import com.towermarsh.opendata.plugin.octopus.extract.ExtractedOctopusStatement;
import com.towermarsh.opendata.plugin.octopus.transform.model.ElectricityRecord;
import com.towermarsh.opendata.plugin.octopus.transform.model.GasRecord;
import java.util.List;
import java.util.Objects;

/**
 * Combined records and source-file provenance produced by one transform batch.
 * @param electricityRecords electricity usage and charges for this batch
 * @param gasRecords gas usage and charges for this batch
 * @param statements source statement metadata and provenance
 *
 * @author Terry Curran
 * @version 4.1.0
 */
public record OctopusParseResult(
        List<ElectricityRecord> electricityRecords,
        List<GasRecord> gasRecords,
        List<ExtractedOctopusStatement> statements) {

    public OctopusParseResult {
        electricityRecords = List.copyOf(Objects.requireNonNull(electricityRecords, "electricityRecords"));
        gasRecords = List.copyOf(Objects.requireNonNull(gasRecords, "gasRecords"));
        statements = List.copyOf(Objects.requireNonNull(statements, "statements"));
    }

    /**
     * Total records.
     *
     * @return result of total records
     */
    public int totalRecords() {
        return electricityRecords.size() + gasRecords.size();
    }
}
