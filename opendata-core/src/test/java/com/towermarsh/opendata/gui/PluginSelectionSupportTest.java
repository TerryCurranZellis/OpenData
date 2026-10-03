/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests shared plugin-table selection helpers.
 *
 * @author Terry Curran
 * @version 3.3.0
 */
class PluginSelectionSupportTest {

    @Test
    void selectsAndCountsAllRows() {
        final var rows = List.of(
                new PluginRow(false, "ofgem", "Ofgem", "Enabled", "", ""),
                new PluginRow(true, "openmeteo", "Open-Meteo", "Enabled", "", ""));

        assertEquals(1, PluginSelectionSupport.countSelected(rows));
        assertTrue(PluginSelectionSupport.anySelected(rows));
        assertFalse(PluginSelectionSupport.allSelected(rows));

        PluginSelectionSupport.setAllSelected(rows, true);

        assertEquals(2, PluginSelectionSupport.countSelected(rows));
        assertTrue(PluginSelectionSupport.allSelected(rows));
    }
}
