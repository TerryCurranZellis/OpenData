/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.electricdemand.extract;

import com.towermarsh.opendata.plugin.neso.NesoCkanClient;
import com.towermarsh.opendata.plugin.neso.NesoCsv;
import com.towermarsh.opendata.plugin.neso.NesoExtraction;
import com.towermarsh.opendata.plugin.neso.NesoResource;
import com.towermarsh.opendata.plugin.neso.NesoSettings;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Extract step: finds the NESO historic demand CSV resources (resource names containing a year inside the configured date range) in the
 * NESO catalogue and downloads them to a temporary working directory.
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public final class ElectricDemandExtract {

    private static final Logger LOG = Logger.getLogger(ElectricDemandExtract.class.getName());

    /**
     * Downloads the matching resources.
     *
     * @param settings NESO settings
     * @return downloaded files
     * @throws IOException on catalogue, download or file failure
     * @throws InterruptedException when interrupted
     */
    public NesoExtraction extract(final NesoSettings settings) throws IOException, InterruptedException {
        Objects.requireNonNull(settings, "settings");
        final var client = new NesoCkanClient(settings);
        final var resources = client.listCsvResources(
                name -> NesoCsv.includesYear(name, settings.startDate(), settings.endDate()));
        if (resources.isEmpty()) {
            throw new IOException("No matching CSV resources found for NESO dataset " + settings.datasetId());
        }
        final var directory = Files.createTempDirectory("opendata-electric-demand-");
        final var downloaded = new ArrayList<NesoResource>();
        try {
            var index = 0;
            for (var resource : resources) {
                downloaded.add(client.download(resource, directory, index++));
            }
        } catch (IOException | InterruptedException | RuntimeException exception) {
            new NesoExtraction(directory, downloaded).cleanUp();
            throw exception;
        }
        LOG.info(() -> "electric-demand extract: downloaded %d resource(s)".formatted(downloaded.size()));
        return new NesoExtraction(directory, downloaded);
    }
}
