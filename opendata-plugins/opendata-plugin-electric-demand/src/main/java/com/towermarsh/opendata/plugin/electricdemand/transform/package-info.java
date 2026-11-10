/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * NESO electricity-demand response parsing and record transformation.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link ElectricDemandTransform} &mdash; Transforms downloaded CSV into demand records.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link DemandRecord} &mdash; One half-hourly demand observation.</li>
 * </ul>
 *
 * @author Terry Curran
 * @since 3.1.0
 * @version 4.1.0
 */
package com.towermarsh.opendata.plugin.electricdemand.transform;
