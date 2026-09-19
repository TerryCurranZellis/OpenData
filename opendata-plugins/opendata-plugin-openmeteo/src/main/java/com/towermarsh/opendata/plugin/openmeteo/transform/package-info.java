/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Transformation support for Open-Meteo datasets.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link OpenMeteoResponseExtractor} &mdash; Parses raw Open-Meteo JSON into the API response model.</li>
 * <li>{@link OpenMeteoTransform} &mdash; Converts downloaded Open-Meteo JSON into validated database records.</li>
 * <li>{@link OpenMeteoTransformer} &mdash; Converts a validated Open-Meteo API response into domain records.</li>
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
package com.towermarsh.opendata.plugin.openmeteo.transform;
