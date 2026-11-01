/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.transform;

import com.towermarsh.opendata.plugin.octopus.transform.model.ElectricityRecord;
import com.towermarsh.opendata.plugin.octopus.transform.model.GasRecord;
import com.towermarsh.opendata.plugin.octopusadjustment.extract.ExtractedOctopusAdjustment;
import java.util.List;
import java.util.Objects;

/**
 * Combined adjustment billing records and source provenance for one batch.
 *
 * @param electricityRecords parsed electricity records
 * @param gasRecords parsed gas records
 * @param sources adjustment PDFs represented by the batch
 *
 * @author Terry Curran
 * @version 3.1.0
 * @since 3.1.0
 */
public record OctopusAdjustmentParseResult(
        List<ElectricityRecord> electricityRecords,
        List<GasRecord> gasRecords,
        List<ExtractedOctopusAdjustment> sources) {

    /** Validates and defensively copies batch content. */
    public OctopusAdjustmentParseResult {
        electricityRecords = List.copyOf(
                Objects.requireNonNull(electricityRecords, "electricityRecords"));
        gasRecords = List.copyOf(Objects.requireNonNull(gasRecords, "gasRecords"));
        sources = List.copyOf(Objects.requireNonNull(sources, "sources"));
    }

    /**
     * Returns the combined electricity and gas record count.
     *
     * @return total record count
     * @since 3.1.0
     */
    public int totalRecords() {
        return electricityRecords.size() + gasRecords.size();
    }
}
