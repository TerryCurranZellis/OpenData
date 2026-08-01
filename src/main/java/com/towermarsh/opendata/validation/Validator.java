/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.validation;

import java.util.List;
import java.util.Map;

/**
 * Defines a data validation operation.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public interface Validator {

    /**
     * Validates a collection of records.
     *
     * @param records records to validate
     * @return validation result
     */
    ValidationResult validate(
            List<Map<String, String>> records);
}
