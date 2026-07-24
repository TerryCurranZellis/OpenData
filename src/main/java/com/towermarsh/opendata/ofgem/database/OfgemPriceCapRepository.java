/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.ofgem.database;

import com.towermarsh.opendata.ofgem.model.OfgemPriceCapLevel;
import com.towermarsh.opendata.ofgem.model.OfgemPriceCapPeriod;
import java.sql.SQLException;
import java.util.List;

/**
 * Persists typed Ofgem price-cap output.
 */
public interface OfgemPriceCapRepository {

    long upsertPeriod(OfgemPriceCapPeriod period, long sourceFileId)
            throws SQLException;

    int replaceLevels(
            long periodId,
            long ingestionRunId,
            List<OfgemPriceCapLevel> levels) throws SQLException;
}
