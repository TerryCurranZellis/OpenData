/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Transformation of adjustment PDFs into ordinary Octopus billing model records.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link com.towermarsh.opendata.plugin.octopusadjustment.transform.OctopusAdjustmentTransform}
 * &mdash; Delegates PDF interpretation to the shared Octopus statement parser.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link com.towermarsh.opendata.plugin.octopusadjustment.transform.OctopusAdjustmentParseResult}
 * &mdash; Combined electricity, gas and adjustment-source batch.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.1.0
 * @since 3.1.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.transform;
