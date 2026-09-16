/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.download;

import com.towermarsh.opendata.model.DataFile;

/**
 * Represents the result of a download operation.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public final class DownloadResult {

    private final boolean successful;
    private final DataFile file;
    private final String message;

    /**
     *
     * @param successful
     * @param file
     * @param message
     */
    public DownloadResult(
            boolean successful,
            DataFile file,
            String message) {

        this.successful = successful;
        this.file = file;
        this.message = message;
    }

    /**
     * Successful result
     * @return
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     * named data file
     * @return
     */
    public DataFile getFile() {
        return file;
    }

    /**
     * message from sender
     * @return
     */
    public String getMessage() {
        return message;
    }
}
