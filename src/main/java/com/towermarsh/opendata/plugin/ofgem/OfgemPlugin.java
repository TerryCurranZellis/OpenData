/*
 * Filename: OfgemPlugin.java
 *
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.download.strategy.ResolvedDownload;
import com.towermarsh.opendata.exception.DownloadException;
import com.towermarsh.opendata.exception.ImportException;
import com.towermarsh.opendata.plugin.OpenDataPlugin;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import com.towermarsh.opendata.plugin.ofgem.config.OfgemConfiguration;
import com.towermarsh.opendata.plugin.ofgem.download.OfgemWorkbookDownloader;
import com.towermarsh.opendata.plugin.ofgem.extract.OfgemPriceCapWorkbookExtractor;
import com.towermarsh.opendata.plugin.ofgem.load.OfgemPersistenceRepository;
import com.towermarsh.opendata.plugin.ofgem.load.OfgemPersistenceResult;
import com.towermarsh.opendata.plugin.ofgem.transform.model.OfgemPriceCapWorkbookData;
import com.towermarsh.opendata.plugin.ofgem.transform.validate.OfgemWorkbookDataValidator;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

/** 
 * Downloads, validates and persists the current Ofgem price-cap workbook. 
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public final class OfgemPlugin implements OpenDataPlugin {
    public static final String PLUGIN_ID = "ofgem";
    private static final Logger LOGGER = Logger.getLogger(OfgemPlugin.class.getName());

    private final OfgemConfiguration configuration;
    private final OfgemWorkbookDownloader downloader;
    private final OfgemPriceCapWorkbookExtractor extractor;
    private final OfgemWorkbookDataValidator validator;

    /**
     * Creates the Ofgem plugin from a resolved plugin definition.
     *
     * @param definition resolved plugin definition
     */
    public OfgemPlugin(final PluginDefinition definition) {
        this(OfgemConfiguration.from(definition));
    }

    /**
     * Creates the Ofgem plugin from typed configuration.
     *
     * @param configuration typed Ofgem configuration
     */
    public OfgemPlugin(final OfgemConfiguration configuration) {
        this(
                configuration,
                new OfgemWorkbookDownloader(configuration),
                new OfgemPriceCapWorkbookExtractor(),
                new OfgemWorkbookDataValidator());
    }

    OfgemPlugin(
            final OfgemConfiguration configuration,
            final OfgemWorkbookDownloader downloader,
            final OfgemPriceCapWorkbookExtractor extractor,
            final OfgemWorkbookDataValidator validator) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
        this.downloader = Objects.requireNonNull(downloader, "downloader");
        this.extractor = Objects.requireNonNull(extractor, "extractor");
        this.validator = Objects.requireNonNull(validator, "validator");
    }

    @Override
    public PluginMetrics execute(final PluginExecutionContext context)
            throws DownloadException, ImportException, IOException {
        Objects.requireNonNull(context, "context");

        final ResolvedDownload download = downloader.download();
        final OfgemPriceCapWorkbookData workbookData =
                validator.validate(extractor.extract(download.localFile()));
        final int recordCount = workbookData.levels().size();

        LOGGER.info(() -> "Ofgem extracted %d price-cap records for %s"
                .formatted(recordCount, workbookData.period().periodName()));

        if (context.dryRun()) {
            return new PluginMetrics(recordCount, 0, 0, recordCount);
        }

        downloader.archive(download.localFile(), workbookData.period().effectiveFrom());

        final OfgemPersistenceRepository repository =
                new OfgemPersistenceRepository(context.database());
        final OfgemPersistenceResult result = repository.persist(
                context.definition(), download, workbookData);

        return new PluginMetrics(
                recordCount,
                result.inserted(),
                result.updated(),
                result.skipped());
    }

    /**
     * Returns the typed Ofgem configuration.
     *
     * @return typed Ofgem configuration
     */
    public OfgemConfiguration configuration() {
        return configuration;
    }
}
