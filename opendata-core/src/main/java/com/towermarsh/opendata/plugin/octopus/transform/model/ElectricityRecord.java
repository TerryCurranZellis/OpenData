/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.transform.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 *
 * Immutable record holding one row of extracted electricity billing data,
 * matching the column layout of {@code electric_data.csv}.
 *
 * <h2>Example</h2>
 * <pre>
 *   ElectricityRecord r = new ElectricityRecord(
 *       LocalDate.of(2022, 1, 5), LocalDate.of(2021, 12, 7), LocalDate.of(2022, 1, 5),
 *       "Octopus Exclusive 12M Fixed", LocalDate.of(2021, 12, 4), LocalDate.of(2022, 1, 3),
 *       "2000012845052", "20E5013326",
 *       LocalDate.of(2021, 12, 4), new BigDecimal("2257.0"), "Smart meter reading",
 *       LocalDate.of(2022, 1, 4), new BigDecimal("2481.3"), "Smart meter reading",
 *       new BigDecimal("224.3"), new BigDecimal("15.51"), new BigDecimal("19.23"),
 *       new BigDecimal("5.96"), new BigDecimal("42.78"));
 * </pre>
 *
 * @param billDate                statement date in {@code yyyy-MM-dd} format
 * @param billPeriodStart         overall bill period start in {@code yyyy-MM-dd}
 * @param billPeriodEnd           overall bill period end in {@code yyyy-MM-dd}
 * @param tariffName              Octopus tariff name
 * @param tariffPeriodStart       tariff sub-period start in {@code yyyy-MM-dd}
 * @param tariffPeriodEnd         tariff sub-period end in {@code yyyy-MM-dd}
 * @param mpan                    13-digit Meter Point Administration Number
 * @param meterId                 physical meter serial number
 * @param startReadingDate        opening meter reading date in {@code yyyy-MM-dd}
 * @param startReadingValue       opening meter reading (kWh) as a string
 * @param startReadingType        how the opening reading was obtained
 * @param endReadingDate          closing meter reading date in {@code yyyy-MM-dd}
 * @param endReadingValue         closing meter reading (kWh) as a string
 * @param endReadingType          how the closing reading was obtained
 * @param energyUsedKwh           energy consumed (kWh) as a string; may be negative
 * @param unitRatePKwh            unit rate in pence per kWh as a string
 * @param standingChargeRatePDay  standing charge rate in pence per day as a string
 * @param standingChargeTotalGbp  billed standing charge total (£) as a string
 * @param totalCostGbp            total electricity charge including VAT (£) as a string
 * 
 * @author (C) Copyright Terry Curran 2026. All Rights Reserved.
 * @version 2.0.0
 */
public record ElectricityRecord(
        LocalDate billDate,
        LocalDate billPeriodStart,
        LocalDate billPeriodEnd,
        String tariffName,
        LocalDate tariffPeriodStart,
        LocalDate tariffPeriodEnd,
        String mpan,
        String meterId,
        LocalDate startReadingDate,
        BigDecimal startReadingValue,
        String startReadingType,
        LocalDate endReadingDate,
        BigDecimal endReadingValue,
        String endReadingType,
        BigDecimal energyUsedKwh,
        BigDecimal unitRatePKwh,
        BigDecimal standingChargeRatePDay,
        BigDecimal standingChargeTotalGbp,
        BigDecimal totalCostGbp) {

    /**
     * Validates and normalises record components.
     */
    public ElectricityRecord {
        Objects.requireNonNull(billDate, "billDate");
        Objects.requireNonNull(billPeriodStart, "billPeriodStart");
        Objects.requireNonNull(billPeriodEnd, "billPeriodEnd");
        tariffName = normaliseText(tariffName, "tariffName", false);
        Objects.requireNonNull(tariffPeriodStart, "tariffPeriodStart");
        Objects.requireNonNull(tariffPeriodEnd, "tariffPeriodEnd");
        mpan = normaliseText(mpan, "mpan", true);
        meterId = normaliseText(meterId, "meterId", true);
        Objects.requireNonNull(startReadingDate, "startReadingDate");
        Objects.requireNonNull(startReadingValue, "startReadingValue");
        startReadingType = normaliseText(startReadingType, "startReadingType", false);
        Objects.requireNonNull(endReadingDate, "endReadingDate");
        Objects.requireNonNull(endReadingValue, "endReadingValue");
        endReadingType = normaliseText(endReadingType, "endReadingType", false);
        Objects.requireNonNull(energyUsedKwh, "energyUsedKwh");
        Objects.requireNonNull(unitRatePKwh, "unitRatePKwh");
        Objects.requireNonNull(standingChargeRatePDay, "standingChargeRatePDay");
        Objects.requireNonNull(standingChargeTotalGbp, "standingChargeTotalGbp");
        Objects.requireNonNull(totalCostGbp, "totalCostGbp");
        if (billPeriodStart.isAfter(billPeriodEnd)) {
            throw new IllegalArgumentException("billPeriodStart must not be after billPeriodEnd");
        }
        if (tariffPeriodStart.isAfter(tariffPeriodEnd)) {
            throw new IllegalArgumentException("tariffPeriodStart must not be after tariffPeriodEnd");
        }
        if (startReadingDate.isAfter(endReadingDate)) {
            throw new IllegalArgumentException("startReadingDate must not be after endReadingDate");
        }
        if (!mpan.isBlank() && !mpan.matches("\\d{13}")) {
            throw new IllegalArgumentException("mpan must be blank or 13 digits");
        }
    }

    /**
     * Normalises one textual value.
     *
     * @param value source value
     * @param fieldName field name for error reporting
     * @param blankAllowed whether blank values are allowed
     * @return normalised value
     */
    private static String normaliseText(
            final String value,
            final String fieldName,
            final boolean blankAllowed) {
        Objects.requireNonNull(value, fieldName);
        final var result = value.trim();
        if (!blankAllowed && result.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return result;
    }
}
