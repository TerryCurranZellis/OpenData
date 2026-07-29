/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem.transform.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Effective period represented by an Ofgem price-cap workbook.
 * @param periodName published Ofgem period name
 * @param effectiveFrom period start date
 * @param effectiveTo period end date
 * @param sourceColumnReference source workbook column reference
 * @param current whether the period is marked as current
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public record OfgemPriceCapPeriod(
        String periodName,
        LocalDate effectiveFrom,
        LocalDate effectiveTo,
        Integer sourceColumnReference,
        boolean current) {

    /** Validates and normalises record components. */

    public OfgemPriceCapPeriod {
        periodName = requireText(periodName, "periodName");
        Objects.requireNonNull(effectiveFrom, "effectiveFrom");
        Objects.requireNonNull(effectiveTo, "effectiveTo");
        if (effectiveTo.isBefore(effectiveFrom)) {
            throw new IllegalArgumentException("effectiveTo cannot precede effectiveFrom");
        }
        if (sourceColumnReference != null && sourceColumnReference < 1) {
            throw new IllegalArgumentException("sourceColumnReference must be positive");
        }
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " cannot be blank");
        }
        return value.trim();
    }
}
