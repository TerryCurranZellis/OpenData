/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.exception;

/**
 * Indicates a data import failure.
 *
 *
 * @author Terry Curran
 * @since 1.0.0
 * @version 4.1.0
 */
public class ImportException extends OpenDataException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new import exception.
     *
     * @param message the detail message
     */
    public ImportException(String message) {
        super(message);
    }

    /**
     * Creates a new import exception.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public ImportException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}
