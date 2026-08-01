/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */

/**
 * Octopus Energy personal billing import plugin.
 *
 * <p>Parses Octopus Energy statement PDF files and persists electricity and
 * gas billing records into the OpenData database schema.
 *
 * <p>Plugin pipeline:
 * <ol>
 *   <li>{@code initialise} – configuration loading and step orchestration</li>
 *   <li>{@code extract} – PDF file discovery (placeholder)</li>
 *   <li>{@code transform} – PDF parsing into structured records</li>
 *   <li>{@code load} – database persistence (placeholder)</li>
 *   <li>{@code finalise} – file archiving and statistics (placeholder)</li>
 * </ol>
 *
 * <ul>
 * <li>{@link OctopusPlugin} – main plugin entry point</li>
 * </ul>
 */
package com.towermarsh.opendata.plugin.octopus;
