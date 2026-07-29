/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.exception;

/**
 * Indicates that imported data failed validation.
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public class ValidationException
        extends OpenDataException {

    /**
     * Creates a new validation exception.
     *
     * @param message the detail message
     *
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Creates a new validation exception.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     *
     */
    public ValidationException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}
