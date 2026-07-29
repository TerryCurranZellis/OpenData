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
 * @version 17 July 2026
 */
public class DownloadException
        extends OpenDataException {

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
