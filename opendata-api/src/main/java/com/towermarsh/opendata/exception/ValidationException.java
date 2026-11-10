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
 * @since 1.0.0
 * @version 4.1.0
 */
public class ValidationException extends OpenDataException {

    private static final long serialVersionUID = 1L;

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
