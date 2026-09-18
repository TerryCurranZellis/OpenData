/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.load;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.towermarsh.opendata.config.model.PluginDefinition;
import com.towermarsh.opendata.database.UnavailableDatabaseResourceManager;
import com.towermarsh.opendata.plugin.PluginDescriptor;
import com.towermarsh.opendata.plugin.PluginExecutionContext;
import com.towermarsh.opendata.plugin.octopus.transform.model.ElectricityRecord;
import com.towermarsh.opendata.plugin.octopusadjustment.extract.ExtractedOctopusAdjustment;
import com.towermarsh.opendata.plugin.octopusadjustment.initialise.OctopusAdjustmentConfiguration;
import com.towermarsh.opendata.plugin.octopusadjustment.transform.OctopusAdjustmentParseResult;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Tests adjustment load dry-run isolation. */
class OctopusAdjustmentLoadTest {

    @Test
    void dryRunReportsRecordsWithoutDatabaseAccess() {
        final var source = new ExtractedOctopusAdjustment(
                Path.of("A-5F191685-419015087-1.pdf"),
                "A-5F191685-419015087-1.pdf",
                "0".repeat(64),
                100L);
        final var result = new OctopusAdjustmentParseResult(
                List.of(electricityRecord()),
                List.of(),
                List.of(source));
        final var configuration = new OctopusAdjustmentConfiguration(
                "A-5F191685", Path.of("input"), Path.of("work"), Path.of("archive"));
        final var context = new PluginExecutionContext(
                UUID.randomUUID(),
                new PluginDescriptor(
                        "octopus-adjustment",
                        "Octopus Energy Adjustments",
                        "",
                        "com.towermarsh.opendata.plugin.octopusadjustment.OctopusAdjustmentPlugin",
                        true,
                        1),
                mock(PluginDefinition.class),
                new UnavailableDatabaseResourceManager(),
                Clock.systemUTC(),
                true);

        final var metrics = new OctopusAdjustmentLoad().load(result, configuration, context);

        assertEquals(1, metrics.read());
        assertEquals(0, metrics.inserted());
        assertEquals(0, metrics.updated());
        assertEquals(1, metrics.skipped());
    }

    private static ElectricityRecord electricityRecord() {
        return new ElectricityRecord(
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 8, 1),
                "Flexible Octopus",
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 8, 1),
                "2000012845052",
                "METER-1",
                LocalDate.of(2026, 7, 1),
                new BigDecimal("100.0"),
                "Smart meter reading",
                LocalDate.of(2026, 8, 1),
                new BigDecimal("120.0"),
                "Smart meter reading",
                new BigDecimal("20.0"),
                new BigDecimal("25.0"),
                new BigDecimal("50.0"),
                new BigDecimal("15.5"),
                new BigDecimal("20.5"));
    }
}
