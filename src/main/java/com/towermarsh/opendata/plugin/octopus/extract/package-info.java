/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */

/**
 * Extract step for the Octopus plugin.
 *
 * <p>Responsible for discovering and making available the Octopus Energy
 * statement PDF files that will be parsed by the transform step.
 *
 * <ul>
 * <li>{@link OctopusExtract} – extract step (placeholder; lists available PDFs)</li>
 * <li>{@link PdfTextExtractor} – utility for extracting plain text from a PDF file</li>
 * </ul>
 */
package com.towermarsh.opendata.plugin.octopus.extract;
