/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.exception;

/**
 * Base exception for all OpenData application errors.
 *
 *
 * @author Terry Curran
 * @since 1.0.0
 * @version 4.1.0
 */
public class OpenDataException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new OpenData exception.
     *
     * @param message the detail message
     */
    public OpenDataException(String message) {
        super(message);
    }

    /**
     * Creates a new OpenData exception.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public OpenDataException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}
