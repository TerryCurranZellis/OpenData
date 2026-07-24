/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.ofgem.model;

import java.time.LocalDate;

/**
 * Summary returned after persisting one workbook.
 */
public record OfgemImportResult(
        long priceCapPeriodId,
        LocalDate effectiveFrom,
        LocalDate effectiveTo,
        int levelsLoaded) {
}
