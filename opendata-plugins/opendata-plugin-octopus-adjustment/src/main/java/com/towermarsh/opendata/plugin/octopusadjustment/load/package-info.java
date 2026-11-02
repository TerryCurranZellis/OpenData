/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Adjustment-specific SQL Server persistence.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link com.towermarsh.opendata.plugin.octopusadjustment.load.AbstractAdjustmentUpsertAdapter}
 * &mdash; Common JDBC upsert control flow.</li>
 * <li>{@link com.towermarsh.opendata.plugin.octopusadjustment.load.AdjustmentElectricityRecordUpsertAdapter}
 * &mdash; Electricity bindings targeting only the adjustment table.</li>
 * <li>{@link com.towermarsh.opendata.plugin.octopusadjustment.load.AdjustmentGasRecordUpsertAdapter}
 * &mdash; Gas bindings targeting only the adjustment table.</li>
 * <li>{@link com.towermarsh.opendata.plugin.octopusadjustment.load.OctopusAdjustmentLoad}
 * &mdash; Dry-run and write-mode load boundary.</li>
 * <li>{@link com.towermarsh.opendata.plugin.octopusadjustment.load.OctopusAdjustmentPersistenceRepository}
 * &mdash; Transactional business-data and completion-ledger persistence.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link com.towermarsh.opendata.plugin.octopusadjustment.load.OctopusAdjustmentPersistenceResult}
 * &mdash; Insert, update and skip counts.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.1.0
 * @since 3.1.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.load;
