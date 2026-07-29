/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.validation;

import java.util.Collections;
import java.util.List;

/**
 * Represents the result of a validation operation.
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public final class ValidationResult {

    private final boolean valid;

    private final long recordsChecked;

    private final long recordsRejected;

    private final List<String> errors;

    /**
     * Creates a validation result.
     *
     * @param valid whether validation passed
     * @param recordsChecked number of records checked
     * @param recordsRejected number rejected
     * @param errors validation messages
     */
    public ValidationResult(
            boolean valid,
            long recordsChecked,
            long recordsRejected,
            List<String> errors) {

        this.valid = valid;

        this.recordsChecked = recordsChecked;

        this.recordsRejected = recordsRejected;

        this.errors
                = Collections.unmodifiableList(errors);
    }

    /**
     *
     * Indicates whether validation succeeded.
     *
     * @return {@code true} when validation succeeded
     *
     */
    public boolean isValid() {
        return valid;
    }

    /**
     *
     * Returns the number of checked records.
     *
     * @return checked record count
     *
     */
    public long getRecordsChecked() {
        return recordsChecked;
    }

    /**
     *
     * Returns the number of rejected records.
     *
     * @return rejected record count
     *
     */
    public long getRecordsRejected() {
        return recordsRejected;
    }

    /**
     *
     * Returns validation error messages.
     *
     * @return immutable validation error messages
     *
     */
    public List<String> getErrors() {
        return errors;
    }
}
