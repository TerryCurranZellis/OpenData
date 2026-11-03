/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.electricdemand.transform;

import com.towermarsh.opendata.plugin.neso.NesoCsv;
import com.towermarsh.opendata.plugin.neso.NesoExtraction;
import com.towermarsh.opendata.plugin.neso.NesoSettings;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * Transform step: parses downloaded demand CSV files into {@link DemandRecord}s
 * inside the configured date range.
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public class ElectricDemandTransform {

    private static final Logger LOG = Logger.getLogger(ElectricDemandTransform.class.getName());

    /**
     * Parses all downloaded files.
     *
     * @param extraction downloaded files
     * @param settings NESO settings (date range)
     * @return parsed records
     * @throws IOException when a file cannot be read
     */
    public List<DemandRecord> transform(NesoExtraction extraction, NesoSettings settings)
            throws IOException {
        Objects.requireNonNull(extraction, "extraction");
        Objects.requireNonNull(settings, "settings");
        List<DemandRecord> records = new ArrayList<>();
        for (var resource : extraction.resources()) {
            try (Reader reader = Files.newBufferedReader(resource.file(), StandardCharsets.UTF_8);
                    var csv = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true)
                            .setIgnoreSurroundingSpaces(true).setIgnoreEmptyLines(true).get().parse(reader)) {
                for (var row : csv) {
                    var record = parse(row);
                    if (!record.settlementDate().isBefore(settings.startDate())
                            && !record.settlementDate().isAfter(settings.endDate())) {
                        records.add(record);
                    }
                }
            } catch (IllegalArgumentException exception) {
                throw new IOException("Unable to parse NESO demand file " + resource.name()
                        + ": " + exception.getMessage(), exception);
            }
        }
        LOG.info(() -> "electric-demand transform: files=%d, records=%d"
                .formatted(extraction.resources().size(), records.size()));
        return List.copyOf(records);
    }

    static DemandRecord parse(CSVRecord row) {
        var date = NesoCsv.parseDate(NesoCsv.field(row, "SETTLEMENT_DATE", "Settlement Date"));
        var period = Integer.parseInt(NesoCsv.field(row, "SETTLEMENT_PERIOD", "Settlement Period"));
        var values = Arrays.asList(
                NesoCsv.number(row, "ND", "National Demand"),
                NesoCsv.number(row, "TSD", "Transmission System Demand"),
                NesoCsv.number(row, "ENGLAND_WALES_DEMAND", "England and Wales Demand"),
                NesoCsv.number(row, "EMBEDDED_WIND_GENERATION", "Estimated Embedded Wind Generation"),
                NesoCsv.number(row, "EMBEDDED_SOLAR_GENERATION", "Estimated Embedded Solar Generation"),
                NesoCsv.number(row, "PUMP_STORAGE_PUMPING", "Pump Storage Pumping"));
        return new DemandRecord(date, period, values);
    }
}
