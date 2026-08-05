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
 * <li>{@link OctopusExtract} – extract step lists available PDFs</li>
 * <li>{@link PdfTextExtractor} – utility for extracting plain text from a PDF file</li>
 * <li>{@link ExtractedOctopusStatement} - Immutable content and provenance for one extracted Octopus statement.</li>
 * <li>{@link OctopusProcessedFileRepository} - Reads the names and hashes of successfully processed Octopus statements..</li>
 * </ul>
 */
package com.towermarsh.opendata.plugin.octopus.extract;
