/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Discovery and source provenance for Octopus adjustment PDFs.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link com.towermarsh.opendata.plugin.octopusadjustment.extract.OctopusAdjustmentExtract}
 * &mdash; Discovers account-prefixed PDFs and calculates SHA-256 hashes.</li>
 * <li>{@link com.towermarsh.opendata.plugin.octopusadjustment.extract.OctopusAdjustmentProcessedFileRepository}
 * &mdash; Reads completed filename/hash identities.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link com.towermarsh.opendata.plugin.octopusadjustment.extract.ExtractedOctopusAdjustment}
 * &mdash; Immutable source path, filename, hash and size provenance.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.1.0
 * @since 3.1.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.extract;
