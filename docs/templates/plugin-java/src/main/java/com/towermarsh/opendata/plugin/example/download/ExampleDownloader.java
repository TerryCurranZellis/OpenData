/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example.download;

import com.towermarsh.opendata.download.HttpDataDownloader;
import com.towermarsh.opendata.download.HttpDownloadOptions;
import com.towermarsh.opendata.exception.DownloadException;
import com.towermarsh.opendata.model.DataFile;
import com.towermarsh.opendata.plugin.example.config.ExampleConfiguration;
import java.time.Duration;
import java.util.Objects;

/** Provider acquisition helper used by the extract stage. */
public final class ExampleDownloader {

    private final ExampleConfiguration configuration;
    private final HttpDataDownloader downloader;

    public ExampleDownloader(final ExampleConfiguration configuration) {
        this.configuration = Objects.requireNonNull(
                configuration, "configuration");
        this.downloader = new HttpDataDownloader(new HttpDownloadOptions(
                Duration.ofSeconds(20),
                configuration.requestTimeout(),
                "OpenData-Example/2.0",
                true,
                configuration.maximumBytes()));
    }

    public DataFile download() throws DownloadException {
        return downloader.download(
                configuration.sourceUri(),
                configuration.downloadPath());
    }
}
