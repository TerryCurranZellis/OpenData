/* Copyright © 2026 Terry Curran; SPDX-License-Identifier: Apache-2.0 */
package com.towermarsh.opendata.plugin.ofgem.initialise;

import com.towermarsh.opendata.download.strategy.ResolvedDownload;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.ofgem.extract.OfgemWorkbookDownloader;
import com.towermarsh.opendata.plugin.ofgem.finalise.OfgemFinalise;
import com.towermarsh.opendata.plugin.ofgem.load.OfgemLoad;
import com.towermarsh.opendata.plugin.ofgem.extract.OfgemPriceCapWorkbookExtractor;

import com.towermarsh.opendata.plugin.ofgem.transform.OfgemPriceCapWorkbookData;
import com.towermarsh.opendata.plugin.ofgem.transform.validate.OfgemWorkbookDataValidator;
import java.util.Objects;

/** Sets up Ofgem configuration and controls the complete plugin flow. */
public final class OfgemInitialise {
    private final OfgemConfiguration configuration;
    private final OfgemWorkbookDownloader extract;
    private final OfgemPriceCapWorkbookExtractor transform;
    private final OfgemWorkbookDataValidator validator;
    private final OfgemLoad load;
    private final OfgemFinalise finalise;

    /**
     *
     * @param configuration
     */
    public OfgemInitialise(final OfgemConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
        this.extract = new OfgemWorkbookDownloader(configuration);
        this.transform = new OfgemPriceCapWorkbookExtractor();
        this.validator = new OfgemWorkbookDataValidator();
        this.load = new OfgemLoad();
        this.finalise = new OfgemFinalise(extract);
    }

    /**
     *
     * @param context
     * @return
     * @throws Exception
     */
    public PluginMetrics execute(final PluginExecutionContext context) throws Exception {
        Objects.requireNonNull(context, "context");
        ResolvedDownload download = null;
        OfgemPriceCapWorkbookData records = null;
        PluginMetrics metrics = PluginMetrics.ZERO;
        try {
            download = extract.download();
            records = validator.validate(transform.extract(download.localFile()));
            metrics = load.load(context, download, records);
            return metrics;
        } finally {
            finalise.complete(context, download, records, metrics);
        }
    }

    /**
     *
     * @return
     */
    public OfgemConfiguration configuration() { return configuration; }
}
