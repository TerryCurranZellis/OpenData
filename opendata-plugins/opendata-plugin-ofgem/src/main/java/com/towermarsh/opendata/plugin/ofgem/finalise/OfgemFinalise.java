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

/**
 * Performs Ofgem post-step cleanup and successful-download archiving.
 * @author Terry Curran
 * @version 4.1.0
 */
public final class OfgemFinalise {
    private static final Logger LOGGER = Logger.getLogger(OfgemFinalise.class.getName());
    private OfgemWorkbookDownloader downloader;

    /**
     * Creates finalisation support for a workbook downloader.
     *
     * @param downloader component that archives successfully processed workbooks
     */
    public OfgemFinalise(OfgemWorkbookDownloader downloader) {
        this.downloader = Objects.requireNonNull(downloader, "downloader");
    }

    /**
     * Archives a successfully processed workbook after a writing run.
     *
     * @param context plugin execution context
     * @param download resolved downloaded workbook, or null if unavailable
     * @param data validated price-cap workbook data, or null if unavailable
     * @param metrics execution read and write counts
     * @throws IOException if archiving the workbook fails
     */
    public void complete(PluginExecutionContext context, ResolvedDownload download,
            OfgemPriceCapWorkbookData data, PluginMetrics metrics) throws IOException {
        if (context.dryRun() || download == null || data == null) return;
        downloader.archive(download.localFile(), data.period().effectiveFrom());
        LOGGER.log(Level.INFO, "Ofgem finalise complete; read={0}, inserted={1}, updated={2}, skipped={3}",
                new Object[]{metrics.read(), metrics.inserted(), metrics.updated(), metrics.skipped()});
    }
}
