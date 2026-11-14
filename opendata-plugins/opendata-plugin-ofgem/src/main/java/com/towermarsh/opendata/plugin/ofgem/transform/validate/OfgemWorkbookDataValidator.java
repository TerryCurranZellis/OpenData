/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem.transform.validate;

import com.towermarsh.opendata.exception.ImportException;
import com.towermarsh.opendata.plugin.ofgem.transform.OfgemPriceCapWorkbookData;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Validates invariants that span multiple extracted Ofgem price-cap rows.
 *
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public final class OfgemWorkbookDataValidator {

    /**
     *
     * @param data
     * @return
     * @throws ImportException
     */
    public OfgemPriceCapWorkbookData validate(OfgemPriceCapWorkbookData data)
            throws ImportException {
        Objects.requireNonNull(data, "data");
        Set<String> sourceCells = new HashSet<>();
        Set<String> businessKeys = new HashSet<>();
        for (var level : data.levels()) {
            var sourceCell = level.sourceSheet() + '!' + level.sourceCell();
            if (!sourceCells.add(sourceCell)) {
                throw new ImportException("Duplicate Ofgem source cell: " + sourceCell);
            }
            var businessKey = String.join(
                    "|",
                    level.regionCode(),
                    level.paymentMethodCode(),
                    level.tariffTypeCode(),
                    level.consumptionBasisCode(),
                    Boolean.toString(level.vatIncluded()));
            if (!businessKeys.add(businessKey)) {
                throw new ImportException("Duplicate Ofgem price-cap row: " + businessKey);
            }
        }
        return data;
    }
}
