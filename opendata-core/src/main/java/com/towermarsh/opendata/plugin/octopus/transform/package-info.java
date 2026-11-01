/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Transform step for the Octopus plugin.
 *
 * <p>Parses Octopus Energy statement PDF files (already present in the input
 * directory) into validated {@link OctopusParseResult} records ready for the
 * load step.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link OctopusStatementParser} &mdash; Extract energy data from Octopus energy statements.</li>
 * <li>{@link OctopusTransform} &mdash; Transforms a complete extraction batch without rereading its PDF files.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link OctopusParseResult} &mdash; Combined records and source-file provenance produced by one transform batch.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
package com.towermarsh.opendata.plugin.octopus.transform;
