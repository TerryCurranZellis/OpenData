/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.ofgem.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Effective period represented by an Ofgem price-cap workbook.
 */
public record OfgemPriceCapPeriod(
        String periodName,
        LocalDate effectiveFrom,
        LocalDate effectiveTo,
        Integer sourceColumnReference,
        boolean current) {

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
