/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.electricsupply.transform;

import com.towermarsh.opendata.plugin.neso.NesoCsv;
import com.towermarsh.opendata.plugin.neso.NesoExtraction;
import com.towermarsh.opendata.plugin.neso.NesoSettings;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * Transform step: parses downloaded generation mix CSV files into
 * {@link GenerationMixRecord}s inside the configured date range.
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public class ElectricSupplyTransform {

    private static final Logger LOG = Logger.getLogger(ElectricSupplyTransform.class.getName());

    /**
     * Parses all downloaded files.
     *
     * @param extraction downloaded files
     * @param settings NESO settings (date range)
     * @return parsed records
     * @throws IOException when a file cannot be read
     */
    public List<GenerationMixRecord> transform(NesoExtraction extraction, NesoSettings settings)
            throws IOException {
        Objects.requireNonNull(extraction, "extraction");
        Objects.requireNonNull(settings, "settings");
        List<GenerationMixRecord> records = new ArrayList<>();
        for (var resource : extraction.resources()) {
            try (Reader reader = Files.newBufferedReader(resource.file(), StandardCharsets.UTF_8); var csv = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true)
                    .setIgnoreSurroundingSpaces(true).setIgnoreEmptyLines(true).get().parse(reader)) {
                for (var row : csv) {
                    var record = parse(row);
                    var date = record.dateTimeUtc().atOffset(ZoneOffset.UTC).toLocalDate();
                    if (!date.isBefore(settings.startDate()) && !date.isAfter(settings.endDate())) {
                        records.add(record);
                    }
                }
            } catch (RuntimeException exception) {
                throw new IOException("Unable to parse NESO generation file " + resource.name()
                        + ": " + exception.getMessage(), exception);
            }
        }
        LOG.info(() -> "electric-supply transform: files=%d, records=%d"
                .formatted(extraction.resources().size(), records.size()));
        return List.copyOf(records);
    }

    /**
     * Parse.
     *
     * @param row input row
     * @return result of parse
     */
    static GenerationMixRecord parse(CSVRecord row) {
        var instant = NesoCsv.parseInstant(NesoCsv.field(row, "DATETIME", "Date & Time"));
        var values = Arrays.asList(
                NesoCsv.number(row, "GAS"), NesoCsv.number(row, "COAL"), NesoCsv.number(row, "NUCLEAR"),
                NesoCsv.number(row, "WIND"), NesoCsv.number(row, "WIND_EMB"), NesoCsv.number(row, "HYDRO"),
                NesoCsv.number(row, "IMPORTS"), NesoCsv.number(row, "BIOMASS"), NesoCsv.number(row, "OTHER"),
                NesoCsv.number(row, "SOLAR"), NesoCsv.number(row, "STORAGE"), NesoCsv.number(row, "GENERATION"),
                NesoCsv.number(row, "CARBON_INTENSITY"));
        return new GenerationMixRecord(instant, values);
    }
}
