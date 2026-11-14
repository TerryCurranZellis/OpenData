/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem.transform;

/**
 * Tariff types represented by Ofgem Annex 9 value columns.
 *
 * @author Terry Curran
 * @since 4.2.0
 * @version 4.2.0
 */
public enum OfgemTariffType {
    ELECTRICITY_SINGLE_RATE,
    ELECTRICITY_MULTI_REGISTER,
    GAS,
    DUAL_FUEL
}
