/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */

/**
 * Load step for the Octopus plugin.
 *
 * <p>Persists transformed electricity and gas records into the database.
 * Dry-run execution logs records without writing to the database.
 *
 * <ul>
 * <li>{@link OctopusLoad}</li>
 * <li>{@link OctopusPersistenceRepository}</li>
 * <li>{@link OctopusPersistenceResult}</li>
 * </ul>
 */
package com.towermarsh.opendata.plugin.octopus.load;
