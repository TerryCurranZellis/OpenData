/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Transformation support for Ofgem price-cap datasets.
 *
 * <p>This package currently declares no top-level types.</p>
 *
 * <h2>Enums</h2>
 * <ul>
 * <li>{@link OfgemPaymentMethod} &mdash; Payment method sections.</li>
 * <li>{@link OfgemRegion} &mdash; Charge-restriction regions.</li>
 * <li>{@link OfgemTariffType} &mdash; Tariff categories.</li>
 * <li>{@link OfgemConsumptionBasis} &mdash; Consumption bases.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link OfgemPriceCapLevel} &mdash; One annual cap level from the Ofgem "1a Levelised DTC" worksheet.</li>
 * <li>{@link OfgemPriceCapPeriod} &mdash; Effective period represented by an Ofgem price-cap workbook.</li>
 * <li>{@link OfgemPriceCapWorkbookData} &mdash; Typed result extracted from one Ofgem price-cap workbook.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 4.2.0
 */
package com.towermarsh.opendata.plugin.ofgem.transform;
