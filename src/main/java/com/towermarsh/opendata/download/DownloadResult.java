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
 * @version 17 July 2026
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
     *
     * @return
     */
    public boolean isSuccessful() {
        return successful;
    }

    /**
     *
     * @return
     */
    public DataFile getFile() {
        return file;
    }

    /**
     *
     * @return
     */
    public String getMessage() {
        return message;
    }
}
