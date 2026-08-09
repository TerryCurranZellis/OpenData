package com.towermarsh.opendata.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public final class PluginRow {

    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty category;
    private final StringProperty status;
    private final StringProperty lastUpdated;

    public PluginRow(
            final String id,
            final String name,
            final String category,
            final String status,
            final String lastUpdated) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.category = new SimpleStringProperty(category);
        this.status = new SimpleStringProperty(status);
        this.lastUpdated = new SimpleStringProperty(lastUpdated);
    }

    public StringProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public StringProperty lastUpdatedProperty() {
        return lastUpdated;
    }
}
