/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem.transform.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * One annual cap level from the Ofgem "1a Levelised DTC" worksheet.
 */
public record OfgemPriceCapLevel(
        String regionCode,
        String paymentMethodCode,
        String tariffTypeCode,
        String consumptionBasisCode,
        BigDecimal amountGbp,
        boolean vatIncluded,
        String sourceSheet,
        String sourceCell) {

    public OfgemPriceCapLevel {
        regionCode = requireText(regionCode, "regionCode");
        paymentMethodCode = requireText(paymentMethodCode, "paymentMethodCode");
        tariffTypeCode = requireText(tariffTypeCode, "tariffTypeCode");
        consumptionBasisCode = requireText(
                consumptionBasisCode, "consumptionBasisCode");
        Objects.requireNonNull(amountGbp, "amountGbp");
        if (amountGbp.signum() < 0) {
            throw new IllegalArgumentException("amountGbp cannot be negative");
        }
        sourceSheet = requireText(sourceSheet, "sourceSheet");
        sourceCell = requireText(sourceCell, "sourceCell");
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " cannot be blank");
        }
        return value.trim();
    }
}
