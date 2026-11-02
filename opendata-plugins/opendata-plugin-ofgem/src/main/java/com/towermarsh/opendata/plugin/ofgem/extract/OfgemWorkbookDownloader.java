/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem.extract;

import com.towermarsh.opendata.download.strategy.HtmlLinkDiscoveryStrategy;
import com.towermarsh.opendata.download.strategy.ResolvedDownload;
import com.towermarsh.opendata.exception.DownloadException;
import com.towermarsh.opendata.plugin.ofgem.initialise.OfgemConfiguration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Objects;
import java.util.logging.Logger;

/** Ofgem-specific discovery, download, and optional source-file archiving.  *
* @author Terry Curran
* @version 1.0.0
*/
public final class OfgemWorkbookDownloader {
    private static final Logger LOGGER =
            Logger.getLogger(OfgemWorkbookDownloader.class.getName());

    private final OfgemConfiguration configuration;

    /**
     *
     * @param configuration
     */
    public OfgemWorkbookDownloader(final OfgemConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration, "configuration");
    }

    /**
     *
     * @return
     * @throws DownloadException
     */
    public ResolvedDownload download() throws DownloadException {
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

    /**
     *
     * @param downloadedFile
     * @param effectiveFrom
     * @throws IOException
     */
    public void archive(final Path downloadedFile, final LocalDate effectiveFrom)
            throws IOException {
        Objects.requireNonNull(downloadedFile, "downloadedFile");
        Objects.requireNonNull(effectiveFrom, "effectiveFrom");
        if (!configuration.archiveOriginalFile()) {
            return;
        }
        final Path archive = configuration.archiveDirectory()
                .resolve(effectiveFrom.toString())
                .resolve(configuration.outputFilename())
                .normalize();
        final var archiveParent = archive.getParent();
        if (archiveParent == null) {
            throw new IOException("Archive path must include a parent directory: " + archive);
        }
        Files.createDirectories(archiveParent);
        Files.copy(downloadedFile, archive, StandardCopyOption.REPLACE_EXISTING);
        LOGGER.info(() -> "Archived Ofgem workbook to " + archive.toAbsolutePath());
    }
}
