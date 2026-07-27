/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem.transform.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * One annual cap level from the Ofgem "1a Levelised DTC" worksheet.
 * @param regionCode price-cap region code
 * @param paymentMethodCode payment method code
 * @param tariffTypeCode tariff type code
 * @param consumptionBasisCode consumption basis code
 * @param amountGbp price-cap amount in pounds sterling
 * @param vatIncluded whether the amount includes VAT
 * @param sourceSheet source worksheet name
 * @param sourceCell source cell reference
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

    /** Validates and normalises record components. */

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
