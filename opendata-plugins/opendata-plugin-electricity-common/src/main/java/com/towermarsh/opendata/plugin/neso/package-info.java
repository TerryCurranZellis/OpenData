/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Reusable NESO CKAN access, CSV parsing and typed configuration shared by the electricity plugins.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link NesoCkanClient} &mdash; Discovers and downloads CKAN CSV resources.</li>
 * <li>{@link NesoCsv} &mdash; Common CSV parsing and date handling.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link NesoExtraction} &mdash; Downloaded resources and their working directory.</li>
 * <li>{@link NesoResource} &mdash; Resource metadata and local download path.</li>
 * <li>{@link NesoSettings} &mdash; Shared typed NESO plugin settings.</li>
 * </ul>
 *
 * @author Terry Curran
 * @since 3.1.0
 * @version 4.1.0
 */
package com.towermarsh.opendata.plugin.neso;
