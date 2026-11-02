/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Load step for the Octopus plugin.
 *
 * <p>Persists transformed electricity and gas records into the database.
 * Dry-run execution logs records without writing to the database.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link AbstractOctopusUpsertAdapter} &mdash; Common JDBC implementation for Octopus electricity and gas record upserts.</li>
 * <li>{@link ElectricityRecordUpsertAdapter} &mdash; SQL Server bindings for Octopus electricity billing records.</li>
 * <li>{@link GasRecordUpsertAdapter} &mdash; SQL Server bindings for Octopus gas billing records.</li>
 * <li>{@link OctopusLoad} &mdash; Load the octopus data</li>
 * <li>{@link OctopusPersistenceRepository} &mdash; Transactionally persists an Octopus statement batch and its file ledger.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link OctopusPersistenceResult} &mdash; results of loading the octopus data</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
package com.towermarsh.opendata.plugin.octopus.load;
