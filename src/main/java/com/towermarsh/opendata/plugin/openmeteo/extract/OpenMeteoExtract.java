/* Copyright © 2026 Terry Curran; SPDX-License-Identifier: Apache-2.0 */
package com.towermarsh.opendata.plugin.openmeteo.extract;

import com.towermarsh.opendata.plugin.openmeteo.initialise.OpenMeteoConfiguration;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

/** Downloads Open-Meteo source data and performs no transformation. */
public final class OpenMeteoExtract {
    private final OpenMeteoDownloader downloader;

    /**
     *
     * @param configuration
     */
    public OpenMeteoExtract(final OpenMeteoConfiguration configuration) {
        this.downloader = new OpenMeteoDownloader(Objects.requireNonNull(configuration, "configuration"));
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public String extract() throws IOException { return downloader.download(); }

    /**
     *
     * @param start
     * @param end
     * @return
     * @throws IOException
     */
    public String extract(final LocalDate start, final LocalDate end) throws IOException {
        return downloader.download(start, end);
    }
}
