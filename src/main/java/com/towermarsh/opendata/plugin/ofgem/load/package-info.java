/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * SQL Server persistence for Ofgem price-cap data.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link OfgemLoad} &mdash; Loads transformed Ofgem records into the configured database tables.</li>
 * <li>{@link OfgemPersistenceRepository} &mdash; Transactional SQL Server persistence for one Ofgem workbook.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link OfgemPersistenceResult} &mdash; Persistence counts returned to the generic plugin coordinator.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
package com.towermarsh.opendata.plugin.ofgem.load;
