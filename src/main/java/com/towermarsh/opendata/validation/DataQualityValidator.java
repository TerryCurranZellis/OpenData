/*
 *  Filename: NewClass.java
 * 
 *  (C) Copyright Terry Curran 2026. All rights reserved
 * 
 *  This software is provided 'as-is', without any express or implied
 *  warranty.  In no event will the author be held liable for any damages
 *  arising from the use of this software.
 * 
 *  Permission is granted to anyone to use this software for any purpose,
 *  including commercial applications, and to alter it and redistribute it
 *  freely, subject to the following restrictions:
 * 
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software
 *     in a product, an acknowledgement in the product documentation would be
 *     appreciated but is not required.
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 *  3. This notice may not be removed or altered from any source distribution.
 * 
 *  The author may be contacted by email to the following address:
 * 
 *  terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Performs basic data quality validation.
 *
 * @author terry
 * @author (C) Copyright Terry Curran 2026. All Rights Reserved.
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
