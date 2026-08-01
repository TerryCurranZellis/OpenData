/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Performs basic data quality validation.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public final class DataQualityValidator
        implements Validator {

    private final List<String> requiredColumns;

    /**
     * Creates a validator.
     *
     * @param requiredColumns mandatory fields
     */
    public DataQualityValidator(
            List<String> requiredColumns) {

        this.requiredColumns
                = requiredColumns;
    }

    /**
     * Validates records.
     *
     * @param records records to check
     * @return validation result
     */
    @Override
    public ValidationResult validate(
            List<Map<String, String>> records) {

        List<String> errors
                = new ArrayList<>();

        long rejected = 0;

        for (Map<String, String> record
                : records) {

            boolean failed = false;

            for (String column
                    : requiredColumns) {

                if (!record.containsKey(column)) {

                    errors.add(
                            "Missing column: "
                            + column);

                    failed = true;
                } else if (record.get(column) == null
                        || record.get(column).isBlank()) {

                    errors.add(
                            "Empty value: "
                            + column);

                    failed = true;
                }
            }

            if (failed) {
                rejected++;
            }
        }

        return new ValidationResult(
                rejected == 0,
                records.size(),
                rejected,
                errors);
    }
}
