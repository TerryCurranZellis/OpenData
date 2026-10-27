/* 
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0 
 */
package com.towermarsh.opendata.plugin.electricdemand;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.plugin.OpenDataPlugin;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.PluginMetrics;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * Independent NESO CKAN historic demand ingestion plugin.
 */
public final class ElectricDemandPlugin implements OpenDataPlugin {

    public static final String PLUGIN_ID = "electric-demand";
    private static final Logger LOG = Logger.getLogger(ElectricDemandPlugin.class.getName());
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String SQL = "UPDATE electricdemand.Demand SET NationalDemandMW=?,TransmissionSystemDemandMW=?,EnglandWalesDemandMW=?,EmbeddedWindMW=?,EmbeddedSolarMW=?,PumpingMW=? WHERE SettlementDate=? AND SettlementPeriod=?";
    private static final String INSERT = "INSERT INTO electricdemand.Demand(SettlementDate,SettlementPeriod,NationalDemandMW,TransmissionSystemDemandMW,EnglandWalesDemandMW,EmbeddedWindMW,EmbeddedSolarMW,PumpingMW) VALUES (?,?,?,?,?,?,?,?)";
    private final PluginDefinition definition;

    public ElectricDemandPlugin(PluginDefinition definition) {
        this.definition = Objects.requireNonNull(definition);
        if (!PLUGIN_ID.equals(definition.id())) {
            throw new IllegalArgumentException("Wrong plugin id");
        }
    }

    @Override
    public PluginMetrics execute(PluginExecutionContext context) throws Exception {
        var start = LocalDate.parse(property("start-date", "2009-01-01"));
        var end = LocalDate.parse(property("end-date", "2025-12-31"));
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("end-date precedes start-date");
        }
        var base = property("api.base-url", "https://api.neso.energy/api/3/action/datapackage_show");
        var dataset = property("api.dataset-id", "historic-demand-data");
        var http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(Integer.parseInt(property("http.connect-timeout-seconds", "30")))).build();
        var timeout = Integer.parseInt(property("http.request-timeout-seconds", "180"));
        var manifest = JSON.readTree(http.send(HttpRequest.newBuilder(URI.create(base + "?id=" + dataset)).timeout(Duration.ofSeconds(timeout)).header("User-Agent", "OpenData/3.1").GET().build(), HttpResponse.BodyHandlers.ofString()).body());
        if (!manifest.path("success").asBoolean()) {
            throw new IllegalStateException("CKAN dataset lookup failed: " + dataset);
        }
        List<JsonNode> resources = new ArrayList<>();
        for (var r : manifest.path("result").path("resources")) {
            var name = r.path("name").asText("");
            if ("CSV".equalsIgnoreCase(r.path("format").asText("")) && includesYear(name, start, end)) {
                resources.add(r);
            }
        }
        if (resources.isEmpty()) {
            throw new IllegalStateException("No matching CSV resources for " + dataset);
        }
        var read = 0L;
        var inserted = 0L;
        var updated = 0L;
        var skipped = 0L;
        var dry = context.dryRun();
        try (var conn = context.database().getConnection()) {
            var original = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try (var update = conn.prepareStatement(SQL); var insert = conn.prepareStatement(INSERT)) {
                try {
                    for (var r : resources) {
                        var url = r.path("url").asText();
                        if (!url.startsWith("https://")) {
                            throw new IllegalStateException("Non-HTTPS resource URL: " + url);
                        }
                        LOG.log(Level.INFO, "Reading {0} from NESO", r.path("name").asText());
                        var res = http.send(HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(timeout)).header("User-Agent", "OpenData/3.1").GET().build(), HttpResponse.BodyHandlers.ofInputStream());
                        if (res.statusCode() != 200) {
                            res.body().close();
                            throw new IllegalStateException("NESO HTTP " + res.statusCode());
                        }
                        try (var stream = res.body(); var csv = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setIgnoreSurroundingSpaces(true).setIgnoreEmptyLines(true).get().parse(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                            for (var record : csv) {
                                var date = parseDate(field(record, "SETTLEMENT_DATE", "Settlement Date"));
                                var period = Integer.parseInt(field(record, "SETTLEMENT_PERIOD", "Settlement Period"));
                                if (period < 1 || period > 50) {
                                    throw new IllegalArgumentException("Invalid settlement period: " + period);
                                }
                                if (date.isBefore(start) || date.isAfter(end)) {
                                    continue;
                                }
                                read++;
                                if (dry) {
                                    skipped++;
                                    continue;
                                }
                                var values = new Double[]{number(record, "ND", "National Demand"), number(record, "TSD", "Transmission System Demand"), number(record, "ENGLAND_WALES_DEMAND", "England and Wales Demand"), number(record, "EMBEDDED_WIND_GENERATION", "Estimated Embedded Wind Generation"), number(record, "EMBEDDED_SOLAR_GENERATION", "Estimated Embedded Solar Generation"), number(record, "PUMP_STORAGE_PUMPING", "Pump Storage Pumping")};
                                for (var i = 0; i < values.length; i++) {
                                    if (values[i] == null) {
                                        update.setNull(i + 1, java.sql.Types.DECIMAL);
                                    } else {
                                        update.setDouble(i + 1, values[i]);
                                    }
                                }
                                update.setDate(7, Date.valueOf(date));
                                update.setInt(8, period);
                                insert.setDate(1, Date.valueOf(date));
                                insert.setInt(2, period);
                                for (var i = 0; i < values.length; i++) {
                                    if (values[i] == null) {
                                        insert.setNull(i + 3, java.sql.Types.DECIMAL);
                                    } else {
                                        insert.setDouble(i + 3, values[i]);
                                    }
                                }
                                if (update.executeUpdate() > 0) {
                                    updated++;
                                } else {
                                    insert.executeUpdate();
                                    inserted++;
                                }
                                if ((inserted + updated) % 500 == 0) {
                                    conn.commit();
                                }
                            }
                        }
                    }
                    if (dry) {
                        conn.rollback();
                    } else {
                        conn.commit();
                    }
                } catch (IOException | IllegalArgumentException | IllegalStateException | InterruptedException | SQLException e) {
                    conn.rollback();
                    throw e;
                } finally {
                    conn.setAutoCommit(original);
                }
            }
        }
        LOG.log(Level.INFO, "Rows read={0}, inserted={1}, updated={2}, skipped={3}", new Object[]{read, inserted, updated, skipped});
        return new PluginMetrics(read, inserted, updated, skipped);
    }

    private String property(String name, String defaultValue) {
        return definition.findProperty(name).map(p -> p.value()).filter(v -> !v.isBlank()).orElse(defaultValue);
    }

    static String field(CSVRecord r, String... aliases) {
        for (var s : aliases) {
            if (r.isMapped(s)) {
                return r.get(s).trim();
            }
        }
        throw new IllegalArgumentException("Missing source column: " + String.join(" / ", aliases));
    }

    static Double number(CSVRecord r, String... aliases) {
        var value = field(r, aliases);
        return value.isBlank() || value.equalsIgnoreCase("null") ? null : Double.valueOf(value.replace(",", ""));
    }

    static LocalDate parseDate(String s) {
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException ex) {
            return LocalDate.parse(s, DateTimeFormatter.ofPattern("dd/MM/uuuu"));
        }
    }

    static Instant parseInstant(String s) {
        try {
            return Instant.parse(s);
        } catch (DateTimeParseException ex) {
            try {
                return OffsetDateTime.parse(s).toInstant();
            } catch (DateTimeParseException ex2) {
                return LocalDateTime.parse(s.replace(' ', 'T')).toInstant(java.time.ZoneOffset.UTC);
            }
        }
    }

    static boolean includesYear(String resourceName, LocalDate start, LocalDate end) {
        for (var year = start.getYear(); year <= end.getYear(); year++) {
            if (resourceName.matches("(?s).*\\b" + year + "\\b.*")) {
                return true;
            }
        }
        return false;
    }
}
