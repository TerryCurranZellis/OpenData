package com.towermarsh.opendata.plugin.electricdemand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.towermarsh.opendata.plugin.electricdemand.transform.ElectricDemandTransform;
import com.towermarsh.opendata.plugin.neso.NesoExtraction;
import com.towermarsh.opendata.plugin.neso.NesoResource;
import com.towermarsh.opendata.plugin.neso.NesoSettings;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ElectricDemandPipelineTest {

    @TempDir
    Path directory;

    @Test
    void transformParsesRowsInsideDateRange() throws Exception {
        var file = directory.resolve("resource-000.csv");
        Files.writeString(file, "SETTLEMENT_DATE,SETTLEMENT_PERIOD,ND,TSD,"
                + "ENGLAND_WALES_DEMAND,EMBEDDED_WIND_GENERATION,"
                + "EMBEDDED_SOLAR_GENERATION,PUMP_STORAGE_PUMPING\n"
                + "2025-01-01,1,100,110,90,5,,0\n2024-12-31,1,1,1,1,1,1,1\n");
        var extraction = new NesoExtraction(directory, List.of(new NesoResource("d 2025", "https://x", file)));
        var settings = new NesoSettings(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31),
                "https://example.org", "x", Duration.ofSeconds(1), Duration.ofSeconds(1));
        var records = new ElectricDemandTransform().transform(extraction, settings);
        assertEquals(1, records.size());
        assertEquals(100.0, records.get(0).values().get(0));
        assertEquals(null, records.get(0).values().get(4));
    }

    @Test
    void cleanUpRemovesWorkingFiles() throws Exception {
        var work = Files.createDirectory(directory.resolve("work"));
        Files.writeString(work.resolve("a.csv"), "x");
        new NesoExtraction(work, List.of()).cleanUp();
        assertTrue(Files.notExists(work));
    }

    @Test
    void registrationHookIsDiscoverableByConvention() {
        assertEquals("electric-demand", new RegisterPlugin().pluginId());
    }
}
