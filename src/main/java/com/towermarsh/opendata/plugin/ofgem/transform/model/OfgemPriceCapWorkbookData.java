/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem.transform.model;

import java.util.List;
import java.util.Objects;

/**
 * Typed result extracted from one Ofgem price-cap workbook.
 */
public record OfgemPriceCapWorkbookData(
        OfgemPriceCapPeriod period,
        List<OfgemPriceCapLevel> levels) {

    public OfgemPriceCapWorkbookData {
        Objects.requireNonNull(period, "period");
        levels = List.copyOf(Objects.requireNonNull(levels, "levels"));
        if (levels.isEmpty()) {
            throw new IllegalArgumentException("levels cannot be empty");
        }
    }
}
