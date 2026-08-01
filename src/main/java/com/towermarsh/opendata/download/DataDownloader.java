/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.download;

import com.towermarsh.opendata.exception.DownloadException;
import com.towermarsh.opendata.model.DataFile;
import java.net.URI;

/**
 * Defines the contract for downloading OpenData files.
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public interface DataDownloader {

    /**
     * Downloads data from a remote location.
     *
     * @param sourceUri source location
     * @param destination destination file
     * @return downloaded file information
     * @throws DownloadException if download fails
     */
    DataFile download(
            URI sourceUri,
            java.nio.file.Path destination)
            throws DownloadException;
}
