/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example.extract;

import com.towermarsh.opendata.exception.DownloadException;
import com.towermarsh.opendata.model.DataFile;
import com.towermarsh.opendata.plugin.example.download.ExampleDownloader;
import java.util.Objects;

/** Obtains the example source representation. */
public final class ExampleExtractor {

    private final ExampleDownloader downloader;

    public ExampleExtractor(final ExampleDownloader downloader) {
        this.downloader = Objects.requireNonNull(
                downloader, "downloader");
    }

    public DataFile extract() throws DownloadException {
        return downloader.download();
    }
}
