/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.transform;

import com.towermarsh.opendata.plugin.octopus.transform.model.ElectricityRecord;
import com.towermarsh.opendata.plugin.octopus.transform.model.GasRecord;

import java.util.List;
import java.util.Objects;

/**
 * Holds the combined result of the Octopus transform step.
 *
 * <p>Both electricity and gas records are produced in a single pass over the
 * input PDF files; this record carries both lists so that the load step can
 * persist them together.
 *
 * @param electricityRecords  transformed electricity billing records; never {@code null}
 * @param gasRecords          transformed gas billing records; never {@code null}
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public record OctopusParseResult(
        List<ElectricityRecord> electricityRecords,
        List<GasRecord> gasRecords) {

    /** Validates and normalises record components. */
    public OctopusParseResult {
        electricityRecords = List.copyOf(Objects.requireNonNull(electricityRecords, "electricityRecords"));
        gasRecords = List.copyOf(Objects.requireNonNull(gasRecords, "gasRecords"));
    }

    /**
     * Returns the total number of records (electricity + gas).
     *
     * @return total record count
     */
    public int totalRecords() {
        return electricityRecords.size() + gasRecords.size();
    }
}
