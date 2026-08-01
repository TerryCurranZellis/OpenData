/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.exception;

import com.towermarsh.opendata.exception.OpenDataException;

/**
 * Indicates that OpenMeteo download or response processing failed.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public final class OpenMeteoException extends OpenDataException {

    /**
     * Creates a new Open-Meteo exception.
     *
     * @param message the detail message
     */
    public OpenMeteoException(final String message) {
        super(message);
    }

    /**
     * Creates a new Open-Meteo exception.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public OpenMeteoException(
            final String message,
            final Throwable cause) {
        super(message, cause);
    }
}
