/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.electricsupply.transform;

import java.time.Instant;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
 * One half-hourly GB generation mix observation.
 *
 * @param dateTimeUtc observation instant (UTC)
 * @param values nullable values in order: gas, coal, nuclear, wind, embedded wind,
 * hydro, imports, biomass, other, solar, storage, generation, carbon intensity
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public record GenerationMixRecord(Instant dateTimeUtc, List<Double> values) {

    /** Number of measure columns. */
    public static final int MEASURES = 13;

    /** Validates the record. */
    public GenerationMixRecord {
        if (values.size() != MEASURES) {
            throw new IllegalArgumentException("Expected " + MEASURES + " measures");
        }
        values = Collections.unmodifiableList(new ArrayList<>(values));
    }
}
