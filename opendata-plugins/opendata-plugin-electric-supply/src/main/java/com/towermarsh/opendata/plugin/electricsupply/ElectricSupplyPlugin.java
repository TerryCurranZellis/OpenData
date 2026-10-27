/* 
 * Copyright © 2026 Terry Curran;
 *
 * SPDX-License-Identifier: Apache-2.0 
 *
 */
package com.towermarsh.opendata.plugin.electricsupply;

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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * Independent NESO CKAN historic generation mix ingestion plugin.
 */
public final class ElectricSupplyPlugin implements OpenDataPlugin {

    public static final String PLUGIN_ID = "electric-supply";
    private static final Logger LOG = Logger.getLogger(ElectricSupplyPlugin.class.getName());
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String SQL = "UPDATE electricsupply.GenerationMix SET GasMW=?,CoalMW=?,NuclearMW=?,WindMW=?,EmbeddedWindMW=?,HydroMW=?,ImportsMW=?,BiomassMW=?,OtherMW=?,SolarMW=?,StorageMW=?,GenerationMW=?,CarbonIntensity=? WHERE DateTimeUtc=?";
    private static final String INSERT = "INSERT INTO electricsupply.GenerationMix(DateTimeUtc,GasMW,CoalMW,NuclearMW,WindMW,EmbeddedWindMW,HydroMW,ImportsMW,BiomassMW,OtherMW,SolarMW,StorageMW,GenerationMW,CarbonIntensity) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private final PluginDefinition definition;

    public ElectricSupplyPlugin(PluginDefinition definition) {
        this.definition = Objects.requireNonNull(definition);
        if (!PLUGIN_ID.equals(definition.id())) {
            throw new IllegalArgumentException("Wrong plugin id");
        }
    }

    /**
     *
     * @param context
     * @return
     * @throws Exception
     */
    @Override
    public PluginMetrics execute(PluginExecutionContext context) throws Exception {
        var start = LocalDate.parse(property("start-date", "2009-01-01"));
        var end = LocalDate.parse(property("end-date", "2025-12-31"));
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("end-date precedes start-date");
        }
        var base = property("api.base-url", "https://api.neso.energy/api/3/action/datapackage_show");
        var dataset = property("api.dataset-id", "historic-generation-mix");
        var http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(Integer.parseInt(property("http.connect-timeout-seconds", "30")))).build();
        var timeout = Integer.parseInt(property("http.request-timeout-seconds", "180"));
        var manifest = JSON.readTree(http.send(HttpRequest.newBuilder(URI.create(base + "?id=" + dataset)).timeout(Duration.ofSeconds(timeout)).header("User-Agent", "OpenData/3.1").GET().build(), HttpResponse.BodyHandlers.ofString()).body());
        if (!manifest.path("success").asBoolean()) {
            throw new IllegalStateException("CKAN dataset lookup failed: " + dataset);
        }
        List<JsonNode> resources = new ArrayList<>();
        for (var r : manifest.path("result").path("resources")) {
            var name = r.path("name").asText("");
            if ("CSV".equalsIgnoreCase(r.path("format").asText("")) && name.toLowerCase(Locale.ROOT).contains("generation")) {
                resources.add(r);
            }
        }
        if (resources.isEmpty()) {
            throw new IllegalStateException("No matching CSV resources for " + dataset);
        }
        var read = 0L;
        var inserted = 0L;
        var updated = 0l;
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
                                var key = parseInstant(field(record, "DATETIME", "Date & Time"));
                                var date = key.atOffset(java.time.ZoneOffset.UTC).toLocalDate();
                                if (date.isBefore(start) || date.isAfter(end)) {
                                    continue;
                                }
                                read++;
                                if (dry) {
                                    skipped++;
                                    continue;
                                }
                                var values = new Double[]{number(record, "GAS"), number(record, "COAL"), number(record, "NUCLEAR"), number(record, "WIND"), number(record, "WIND_EMB"), number(record, "HYDRO"), number(record, "IMPORTS"), number(record, "BIOMASS"), number(record, "OTHER"), number(record, "SOLAR"), number(record, "STORAGE"), number(record, "GENERATION"), number(record, "CARBON_INTENSITY")};
                                for (var i = 0; i < values.length; i++) {
                                    if (values[i] == null) {
                                        update.setNull(i + 1, java.sql.Types.DECIMAL);
                                    } else {
                                        update.setDouble(i + 1, values[i]);
                                    }
                                }
                                update.setTimestamp(14, Timestamp.from(key));
                                insert.setTimestamp(1, Timestamp.from(key));
                                for (var i = 0; i < values.length; i++) {
                                    if (values[i] == null) {
                                        insert.setNull(i + 2, java.sql.Types.DECIMAL);
                                    } else {
                                        insert.setDouble(i + 2, values[i]);
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
                } catch (IOException | IllegalStateException | InterruptedException | SQLException e) {
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
