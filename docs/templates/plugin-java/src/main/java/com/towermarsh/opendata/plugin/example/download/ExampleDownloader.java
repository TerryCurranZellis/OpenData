/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.example.download;

import com.towermarsh.opendata.download.HttpDataDownloader;
import com.towermarsh.opendata.exception.DownloadException;
import com.towermarsh.opendata.model.DataFile;
import com.towermarsh.opendata.plugin.example.config.ExampleConfiguration;
import java.util.Objects;

/** Acquires the example source through shared HTTP download infrastructure. */
public final class ExampleDownloader {
    private final ExampleConfiguration configuration;

    public ExampleDownloader(final ExampleConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
    }

    public DataFile download() throws DownloadException {
        return new HttpDataDownloader().download(
                configuration.sourceUri(),
                configuration.downloadPath());
    }
}
