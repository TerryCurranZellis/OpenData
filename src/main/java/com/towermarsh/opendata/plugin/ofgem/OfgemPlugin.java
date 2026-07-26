/*
 * Filename: OfgemPlugin.java
 *
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.download.strategy.HtmlLinkDiscoveryStrategy;
import com.towermarsh.opendata.download.strategy.ResolvedDownload;
import com.towermarsh.opendata.exception.DownloadException;
import com.towermarsh.opendata.exception.ImportException;
import com.towermarsh.opendata.ofgem.OfgemPriceCapWorkbookExtractor;
import com.towermarsh.opendata.ofgem.model.OfgemPriceCapWorkbookData;
import com.towermarsh.opendata.plugin.OpenDataPlugin;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Objects;
import java.util.logging.Logger;

/** 
 * Downloads, validates and persists the current Ofgem price-cap workbook. 
 */
public final class OfgemPlugin implements OpenDataPlugin {
    public static final String PLUGIN_ID = "ofgem";
    private static final Logger LOGGER = Logger.getLogger(OfgemPlugin.class.getName());

    private final OfgemConfiguration configuration;
    private final OfgemPriceCapWorkbookExtractor extractor;

    public OfgemPlugin(final PluginDefinition definition) {
        this(OfgemConfiguration.from(definition));
    }

    public OfgemPlugin(final OfgemConfiguration configuration) {
        this(configuration, new OfgemPriceCapWorkbookExtractor());
    }

    OfgemPlugin(
            final OfgemConfiguration configuration,
            final OfgemPriceCapWorkbookExtractor extractor) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
        this.extractor = Objects.requireNonNull(extractor, "extractor");
    }

    @Override
    public PluginMetrics execute(final PluginExecutionContext context)
            throws DownloadException, ImportException, IOException {
        Objects.requireNonNull(context, "context");

        final ResolvedDownload download = downloadWorkbook();
        final OfgemPriceCapWorkbookData workbookData = extractor.extract(download.localFile());
        final int recordCount = workbookData.levels().size();

        LOGGER.info(() -> "Ofgem extracted %d price-cap records for %s"
                .formatted(recordCount, workbookData.period().periodName()));

        if (context.dryRun()) {
            return new PluginMetrics(recordCount, 0, 0, recordCount);
        }

        archiveIfRequired(download.localFile(), workbookData.period().effectiveFrom());

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

    private ResolvedDownload downloadWorkbook() throws DownloadException {
        final var endpoint = configuration.publicationEndpoint();
        final var discovery = endpoint.linkDiscovery().orElseThrow(() ->
                new IllegalArgumentException("Ofgem publication endpoint requires link discovery"));
        LOGGER.info(() -> "Discovering current Ofgem price-cap workbook from " + endpoint.uri());
        return new HtmlLinkDiscoveryStrategy(configuration.connectTimeout()).download(
                endpoint.uri(),
                configuration.downloadPath(),
                endpoint.headers(),
                configuration.requestTimeout(),
                discovery);
    }

    private void archiveIfRequired(final Path downloadedFile, final LocalDate effectiveFrom)
            throws IOException {
        if (!configuration.archiveOriginalFile()) {
            return;
        }
        final Path archive = configuration.archiveDirectory()
                .resolve(effectiveFrom.toString())
                .resolve(configuration.outputFilename())
                .normalize();
        Files.createDirectories(archive.getParent());
        Files.copy(downloadedFile, archive, StandardCopyOption.REPLACE_EXISTING);
        LOGGER.info(() -> "Archived Ofgem workbook to " + archive.toAbsolutePath());
    }

    public OfgemConfiguration configuration() {
        return configuration;
    }
}
