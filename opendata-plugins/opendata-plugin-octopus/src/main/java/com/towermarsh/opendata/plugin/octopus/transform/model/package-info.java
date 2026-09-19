/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Transformed Octopus Energy billing records produced by the transform step.
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link ElectricityRecord} &mdash; Immutable record holding one row of extracted electricity billing data, matching the column layout of {@code electric_data.csv}.</li>
 * <li>{@link GasRecord} &mdash; Immutable record holding one row of extracted gas billing data, matching the column layout of {@code gas_data.csv}.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
package com.towermarsh.opendata.plugin.octopus.transform.model;
