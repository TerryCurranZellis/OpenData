/* Copyright © 2026 Terry Curran; SPDX-License-Identifier: Apache-2.0 */
package com.towermarsh.opendata.plugin.ofgem.finalise;

import com.towermarsh.opendata.download.strategy.ResolvedDownload;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.ofgem.extract.OfgemWorkbookDownloader;
import com.towermarsh.opendata.plugin.ofgem.transform.OfgemPriceCapWorkbookData;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Performs Ofgem post-step cleanup and successful-download archiving. */
public final class OfgemFinalise {
    private static final Logger LOGGER = Logger.getLogger(OfgemFinalise.class.getName());
    private final OfgemWorkbookDownloader downloader;

    /**
     *
     * @param downloader
     */
    public OfgemFinalise(final OfgemWorkbookDownloader downloader) {
        this.downloader = Objects.requireNonNull(downloader, "downloader");
    }

    /**
     *
     * @param context
     * @param download
     * @param data
     * @param metrics
     * @throws IOException
     */
    public void complete(final PluginExecutionContext context, final ResolvedDownload download,
            final OfgemPriceCapWorkbookData data, final PluginMetrics metrics) throws IOException {
        if (context.dryRun() || download == null || data == null) return;
        downloader.archive(download.localFile(), data.period().effectiveFrom());
        LOGGER.log(Level.INFO, "Ofgem finalise complete; read={0}, inserted={1}, updated={2}, skipped={3}",
                new Object[]{metrics.read(), metrics.inserted(), metrics.updated(), metrics.skipped()});
    }
}
