/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Extract step for the Octopus plugin.
 *
 * <p>Responsible for discovering and making available the Octopus Energy
 * statement PDF files that will be parsed by the transform step.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link OctopusExtract} &mdash; Discovers, filters and reads local Octopus Energy statement PDFs.</li>
 * <li>{@link OctopusProcessedFileRepository} &mdash; Reads the names and hashes of successfully processed Octopus statements.</li>
 * <li>{@link PdfTextExtractor} &mdash; Extracts plain text from a PDF file using Apache PDFBox 3.x.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link ExtractedOctopusStatement} &mdash; Immutable content and provenance for one extracted Octopus statement.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
package com.towermarsh.opendata.plugin.octopus.extract;
