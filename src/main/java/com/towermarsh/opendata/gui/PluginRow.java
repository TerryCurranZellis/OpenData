/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Presentation model for one plugin displayed in the JavaFX main-window table.
 *
 * <p>This type deliberately contains presentation data only. Batch 1 uses
 * sample rows so the main page can be developed without coupling the JavaFX
 * layer to the database-backed plugin registry.</p>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
public final class PluginRow {

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
     * @param lastRunDate date/time of the most recent run, or blank if never run
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
     * Selection property used by the table checkbox.
     *
     * @return selection property
     */
    public BooleanProperty selectedProperty() {
        return selected;
    }

    /**
     * Plugin identifier property.
     *
     * @return plugin identifier property
     */
    public StringProperty pluginIdProperty() {
        return pluginId;
    }

    /**
     * Plugin description property.
     *
     * @return description property
     */
    public StringProperty descriptionProperty() {
        return description;
    }

    /**
     * Enabled-state display property.
     *
     * @return enabled-state property
     */
    public StringProperty enabledStateProperty() {
        return enabledState;
    }

    /**
     * Most-recent run status property.
     *
     * @return run status property
     */
    public StringProperty lastRunStatusProperty() {
        return lastRunStatus;
    }

    /**
     * Most-recent run date property.
     *
     * @return run date property
     */
    public StringProperty lastRunDateProperty() {
        return lastRunDate;
    }
}
