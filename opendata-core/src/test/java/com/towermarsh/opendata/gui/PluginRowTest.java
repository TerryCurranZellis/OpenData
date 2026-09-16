/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.towermarsh.opendata.plugin.PluginRunStatus;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Tests conversion from backend table data to JavaFX display values.
 *
 * @author Terry Curran
 * @version 3.0.0
 */
class PluginRowTest {

    @Test
    void mapsPersistentStateToDisplayValues() {
        final var entry = new PluginTableEntry(
                "ofgem",
                "Ofgem Energy Price Cap",
                true,
                Optional.of(PluginRunStatus.SUCCESS),
                Optional.of(LocalDateTime.of(2026, 8, 13, 18, 45)));

        final var row = PluginRow.from(entry);

        assertEquals("ofgem", row.pluginIdProperty().get());
        assertEquals("Ofgem Energy Price Cap", row.descriptionProperty().get());
        assertEquals("Enabled", row.enabledStateProperty().get());
        assertEquals("Success", row.lastRunStatusProperty().get());
    }

    @Test
    void keepsRunColumnsBlankForPluginThatHasNeverRun() {
        final var entry = new PluginTableEntry(
                "octopus",
                "Octopus Energy Statements",
                false,
                Optional.empty(),
                Optional.empty());

        final var row = PluginRow.from(entry);

        assertEquals("Disabled", row.enabledStateProperty().get());
        assertEquals("", row.lastRunStatusProperty().get());
        assertEquals("", row.lastRunDateProperty().get());
    }
}
