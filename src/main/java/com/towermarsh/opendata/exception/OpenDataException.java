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
 * @version 17 July 2026
 */
public class OpenDataException extends Exception {

    /**
     * Creates a new exception.
     *
     * @param message error description
     */
    /**
     * Creates a new OpenData exception.
     *
     * @param message the detail message
     */
    public OpenDataException(String message) {
        super(message);
    }

    /**
     * Creates a new exception with a cause.
     *
     * @param message error description
     * @param cause underlying exception
     */
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
