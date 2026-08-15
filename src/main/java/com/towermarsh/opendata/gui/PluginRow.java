/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Presentation model for one plugin displayed in the JavaFX main-window table.
 *
 * <p>
 * Batch 3 maps persistent registry and run-audit information into this
 * JavaFX-specific model. Database objects remain outside the presentation
 * layer.</p>
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public final class PluginRow {

    private static final DateTimeFormatter LAST_RUN_FORMATTER
            = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.UK);

    private final BooleanProperty selected;
    private final StringProperty pluginId;
    private final StringProperty description;
    private final StringProperty enabledState;
    private final StringProperty lastRunStatus;
    private final StringProperty lastRunDate;

    /**
     * Creates a row for display in the plugin table.
     *
     * @param selected whether the row is selected for a future action
     * @param pluginId plugin identifier
     * @param description plugin description
     * @param enabledState enabled or disabled state for display
     * @param lastRunStatus status of the most recent run, or blank if never run
     * @param lastRunDate date/time of the most recent run, or blank if never
     * run
     */
    public PluginRow(
            final boolean selected,
            final String pluginId,
            final String description,
            final String enabledState,
            final String lastRunStatus,
            final String lastRunDate) {
        this.selected = new SimpleBooleanProperty(selected);
        this.pluginId = new SimpleStringProperty(pluginId);
        this.description = new SimpleStringProperty(description);
        this.enabledState = new SimpleStringProperty(enabledState);
        this.lastRunStatus = new SimpleStringProperty(lastRunStatus);
        this.lastRunDate = new SimpleStringProperty(lastRunDate);
    }

    /**
     * Converts one backend table entry into a JavaFX presentation row.
     *
     * <p>
     * Plugin run timestamps are stored in SQL Server as UTC. They are shown
     * using the workstation's local time zone.</p>
     *
     * @param entry backend table entry
     * @return JavaFX row
     */
    public static PluginRow from(final PluginTableEntry entry) {
        Objects.requireNonNull(entry, "entry");
        final var status = entry.lastRunStatus()
                .map(value -> titleCase(value.displayName()))
                .orElse("");
        final var date = entry.lastRunStartedAtUtc()
                .map(value -> value
                        .atOffset(ZoneOffset.UTC)
                        .atZoneSameInstant(ZoneId.systemDefault())
                        .format(LAST_RUN_FORMATTER))
                .orElse("");
        return new PluginRow(
                false,
                entry.pluginId(),
                entry.description(),
                entry.enabled() ? "Enabled" : "Disabled",
                status,
                date);
    }

    private static String titleCase(final String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    /**
     * Selection property used by the table checkbox.
     *
     * @return selection property
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP",
            justification = "JavaFX property accessor intentionally exposes the live property for binding."
    )
    public BooleanProperty selectedProperty() {
        return selected;
    }

    /**
     * Plugin identifier property.
     *
     * @return plugin identifier property
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP",
            justification = "JavaFX property accessor intentionally exposes the live property for binding."
    )
    public StringProperty pluginIdProperty() {
        return pluginId;
    }

    /**
     * Plugin description property.
     *
     * @return description property
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP",
            justification = "JavaFX property accessor intentionally exposes the live property for binding."
    )
    public StringProperty descriptionProperty() {
        return description;
    }

    /**
     * Enabled-state display property.
     *
     * @return enabled-state property
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP",
            justification = "JavaFX property accessor intentionally exposes the live property for binding."
    )
    public StringProperty enabledStateProperty() {
        return enabledState;
    }

    /**
     * Most-recent run status property.
     *
     * @return run status property
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP",
            justification = "JavaFX property accessor intentionally exposes the live property for binding."
    )
    public StringProperty lastRunStatusProperty() {
        return lastRunStatus;
    }

    /**
     * Most-recent run date property.
     *
     * @return run date property
     */
    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP",
            justification = "JavaFX property accessor intentionally exposes the live property for binding."
    )
    public StringProperty lastRunDateProperty() {
        return lastRunDate;
    }
}
