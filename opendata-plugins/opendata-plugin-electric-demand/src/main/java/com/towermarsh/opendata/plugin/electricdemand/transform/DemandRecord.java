/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.electricdemand.transform;

import java.time.LocalDate;
import java.util.List;

/**
 * One half-hourly GB demand observation.
 *
 * @param settlementDate settlement date
 * @param settlementPeriod settlement period (1-50)
 * @param values nullable MW values in order: national demand, transmission system
 * demand, England and Wales demand, embedded wind, embedded solar, pumping
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public record DemandRecord(LocalDate settlementDate, int settlementPeriod, List<Double> values) {

    /** Number of measure columns. */
    public static final int MEASURES = 6;

    /** Validates the record. */
    public DemandRecord {
        if (settlementPeriod < 1 || settlementPeriod > 50) {
            throw new IllegalArgumentException("Invalid settlement period: " + settlementPeriod);
        }
        if (values.size() != MEASURES) {
            throw new IllegalArgumentException("Expected " + MEASURES + " measures");
        }
        values = java.util.Collections.unmodifiableList(new java.util.ArrayList<>(values));
    }
}
