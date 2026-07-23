/*
 * Filename: DataQualityValidator.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The author may be contacted by email to the following address:
 *
 * terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Performs basic data quality validation.
 *
 * @author Terry Curran
 * @version 21 Jul 2026
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
