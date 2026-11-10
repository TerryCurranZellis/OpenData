/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import java.util.List;
import java.util.Objects;

/**
 * Shared selection helpers for the JavaFX plugin table.
 *
 * @author Terry Curran
 * @version 4.1.0
 * @since 3.3.0
 */
public final class PluginSelectionSupport {

    private PluginSelectionSupport() {
        // Utility class.
    }

    /**
     * Counts selected rows.
     *
     * @param rows plugin rows to inspect
     * @return selected-row count
     */
    public static long countSelected(List<PluginRow> rows) {
        return snapshot(rows).stream()
                .filter(PluginRow::isSelected)
                .count();
    }

    /**
     * Indicates whether at least one row is selected.
     *
     * @param rows plugin rows to inspect
     * @return {@code true} when one or more rows are selected
     */
    public static boolean anySelected(List<PluginRow> rows) {
        return snapshot(rows).stream().anyMatch(PluginRow::isSelected);
    }

    /**
     * Indicates whether every row is selected.
     *
     * @param rows plugin rows to inspect
     * @return {@code true} when the list is non-empty and every row is selected
     */
    public static boolean allSelected(List<PluginRow> rows) {
        var snapshot = snapshot(rows);
        return !snapshot.isEmpty() && snapshot.stream().allMatch(PluginRow::isSelected);
    }

    /**
     * Applies the same selected state to every row.
     *
     * @param rows plugin rows to update
     * @param selected selection state to apply
     */
    public static void setAllSelected(
            List<PluginRow> rows,
            final boolean selected) {
        snapshot(rows).forEach(row -> row.setSelected(selected));
    }

    /**
     * Snapshot.
     *
     * @param rows input rows
     * @return result of snapshot
     */
    private static List<PluginRow> snapshot(List<PluginRow> rows) {
        return List.copyOf(Objects.requireNonNull(rows, "rows"));
    }
}
