/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.ofgem;

import com.towermarsh.opendata.exception.ImportException;
import com.towermarsh.opendata.ofgem.database.OfgemPriceCapRepository;
import com.towermarsh.opendata.ofgem.model.OfgemImportResult;
import com.towermarsh.opendata.ofgem.model.OfgemPriceCapWorkbookData;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Coordinates typed workbook extraction and transactional SQL persistence.
 */
public final class OfgemPriceCapImportService {

    private final OfgemPriceCapWorkbookExtractor extractor;
    private final OfgemPriceCapRepository repository;

    public OfgemPriceCapImportService(
            OfgemPriceCapWorkbookExtractor extractor,
            OfgemPriceCapRepository repository) {
        this.extractor = Objects.requireNonNull(extractor, "extractor");
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public OfgemImportResult importWorkbook(
            Path workbook,
            long ingestionRunId,
            long sourceFileId) throws ImportException {
        OfgemPriceCapWorkbookData data = extractor.extract(workbook);
        try {
            long periodId = repository.upsertPeriod(data.period(), sourceFileId);
            int loaded = repository.replaceLevels(
                    periodId,
                    ingestionRunId,
                    data.levels());
            return new OfgemImportResult(
                    periodId,
                    data.period().effectiveFrom(),
                    data.period().effectiveTo(),
                    loaded);
        } catch (SQLException exception) {
            throw new ImportException("Unable to persist Ofgem price-cap data", exception);
        }
    }
}
