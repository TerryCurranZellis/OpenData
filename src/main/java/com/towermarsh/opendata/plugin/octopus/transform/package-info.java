/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */

/**
 * Transform step for the Octopus plugin.
 *
 * <p>Parses Octopus Energy statement PDF files (already present in the input
 * directory) into validated {@link OctopusParseResult} records ready for the
 * load step.
 *
 * <ul>
 * <li>{@link OctopusStatementParser} – regex-based PDF text parser</li>
 * <li>{@link OctopusTransform} – transform step orchestrator</li>
 * <li>{@link OctopusParseResult} – combined electricity and gas result holder</li>
 * </ul>
 */
package com.towermarsh.opendata.plugin.octopus.transform;
