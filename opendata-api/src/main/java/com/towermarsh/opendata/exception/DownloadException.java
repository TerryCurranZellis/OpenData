/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.exception;

/**
 * Indicates an error downloading OpenData files.
 *
 * @author Terry Curran
 * @since 1.0.0
 * @version 4.1.0
 */
public class DownloadException extends OpenDataException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new download exception.
     *
     * @param message the detail message
     */
    public DownloadException(String message) {
        super(message);
    }

    /**
     * Creates a new download exception.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public DownloadException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}
