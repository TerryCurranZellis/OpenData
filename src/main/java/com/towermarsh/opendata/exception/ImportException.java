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
 * @version 17 July 2026
 */
public class ImportException
        extends OpenDataException {

    /**
     * Creates a new import exception.
     *
     * @param message the detail message
     */
    public ImportException(String message) {
        super(message);
    }

    /**
     * s a new import exception.
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
