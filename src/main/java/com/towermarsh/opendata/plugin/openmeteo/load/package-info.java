/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * SQL Server persistence for Open-Meteo data.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link OpenMeteoLoad} &mdash; Loads transformed Open-Meteo records into the database.</li>
 * <li>{@link OpenMeteoRepository} &mdash; Transactional and idempotent SQL Server writer for Open-Meteo daily data.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link OpenMeteoPersistenceResult} &mdash; SQL Server row counts for one Open-Meteo load.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
package com.towermarsh.opendata.plugin.openmeteo.load;
