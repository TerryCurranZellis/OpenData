/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.extract;

import com.towermarsh.opendata.exception.PluginException;
import com.towermarsh.opendata.plugin.octopus.initialise.OctopusConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Extract step for the Octopus plugin.
 *
 * <p><b>Placeholder implementation.</b> In a full implementation this step
 * would be responsible for:
 * <ol>
 *   <li>Connecting to an email account, cloud storage, or other source to
 *       discover new Octopus Energy statement PDFs.</li>
 *   <li>Downloading the PDFs into the configured input directory.</li>
 *   <li>Verifying that each downloaded file is a valid, readable PDF.</li>
 * </ol>
 *
 * <p>The current placeholder implementation simply lists the PDF files that
 * are already present in the input directory without attempting any download.
 * This allows the downstream transform and load steps to be exercised without
 * a live email or cloud connection.
 *
 * <p>Text extraction from the PDFs is handled by {@link PdfTextExtractor} and
 * is used inside the transform step via {@link com.towermarsh.opendata.plugin.octopus.transform.OctopusStatementParser}.
 *
 * @author Terry Curran
 * @version 01 Aug 2026
 */
public final class OctopusExtract {

    private static final Logger LOGGER = Logger.getLogger(OctopusExtract.class.getName());
    private static final String PDF_SUFFIX = ".pdf";

    /**
     * Returns a list of PDF files available in the configured input directory.
     *
     * <p><b>Placeholder:</b> a full implementation would download new PDFs
     * before returning this list.
     *
     * @param configuration Octopus plugin configuration
     * @return list of PDF file paths found in the input directory; never
     *         {@code null}; may be empty if no PDFs are present
     * @throws PluginException if the input directory cannot be read
     */
    public List<Path> extract(final OctopusConfiguration configuration)
            throws PluginException {
        Objects.requireNonNull(configuration, "configuration");

        final Path inputDir = configuration.inputDirectory();
        LOGGER.info(() -> "Octopus extract: scanning for PDF files in " + inputDir);

        try (Stream<Path> stream = Files.list(inputDir)) {
            final List<Path> pdfs = stream
                    .filter(p -> p.getFileName().toString()
                            .toLowerCase(java.util.Locale.ROOT).endsWith(PDF_SUFFIX))
                    .sorted()
                    .toList();

            LOGGER.info(() -> "Octopus extract: found %d PDF file(s)".formatted(pdfs.size()));
            return pdfs;
        } catch (IOException e) {
            throw new PluginException("octopus",
                    "Failed to list PDF files in input directory: " + inputDir, e);
        }
    }
}
