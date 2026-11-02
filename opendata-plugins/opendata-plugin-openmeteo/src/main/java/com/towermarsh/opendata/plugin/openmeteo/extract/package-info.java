/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Open-Meteo response extraction models
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link OpenMeteoDownloader} &mdash; Downloads raw JSON from the Open-Meteo archive API.</li>
 * <li>{@link OpenMeteoExtract} &mdash; Downloads Open-Meteo source data and performs no transformation.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link OpenMeteoResponse} &mdash; Jackson response model for the Open-Meteo historical weather API.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
package com.towermarsh.opendata.plugin.openmeteo.extract;
