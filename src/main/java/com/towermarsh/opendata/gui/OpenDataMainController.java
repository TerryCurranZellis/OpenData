/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;

/**
 * Controller for {@code OpenDataMainView.fxml}.
 *
 * <p>Batch 1 is intentionally presentation-only. Menu and toolbar handlers
 * demonstrate the intended event wiring and update the status bar, but they do
 * not call the database, plugin registry, configuration services or execution
 * coordinator. Those integrations are introduced in later GUI batches.</p>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
public final class OpenDataMainController {

    @FXML
    private TableView<PluginRow> pluginTable;

    @FXML
    private TableColumn<PluginRow, Boolean> selectedColumn;

    @FXML
    private TableColumn<PluginRow, String> pluginIdColumn;

    @FXML
    private TableColumn<PluginRow, String> descriptionColumn;

    @FXML
    private TableColumn<PluginRow, String> enabledColumn;

    @FXML
    private TableColumn<PluginRow, String> lastRunStatusColumn;

    @FXML
    private TableColumn<PluginRow, String> lastRunDateColumn;

    @FXML
    private Label stateLabel;

    @FXML
    private Label selectedLabel;

    /**
     * Configures the main page once the FXML has been loaded.
     */
    @FXML
    private void initialize() {
        setState("Loading plugin details...");

        pluginTable.setEditable(true);
        pluginTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        selectedColumn.setCellValueFactory(data -> data.getValue().selectedProperty());
        selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));
        pluginIdColumn.setCellValueFactory(data -> data.getValue().pluginIdProperty());
        descriptionColumn.setCellValueFactory(data -> data.getValue().descriptionProperty());
        enabledColumn.setCellValueFactory(data -> data.getValue().enabledStateProperty());
        lastRunStatusColumn.setCellValueFactory(data -> data.getValue().lastRunStatusProperty());
        lastRunDateColumn.setCellValueFactory(data -> data.getValue().lastRunDateProperty());

        final var rows = FXCollections.observableArrayList(
                new PluginRow(false, "ofgem", "Ofgem Energy Price Cap", "Enabled", "Success", "08 Aug 2026 15:56"),
                new PluginRow(false, "openmeteo", "Open-Meteo Weather History", "Enabled", "Success", "08 Aug 2026 15:54"),
                new PluginRow(false, "octopus", "Octopus Energy Statements", "Disabled", "", ""));

        rows.forEach(row -> row.selectedProperty().addListener(
                (observable, oldValue, newValue) -> updateSelectionCount()));
        pluginTable.setItems(rows);

        updateSelectionCount();
        Platform.runLater(() -> setState("Ready"));
    }

    /**
     * Updates the lower-right count from the explicit checkbox state.
     */
    private void updateSelectionCount() {
        final long count = pluginTable.getItems().stream()
                .filter(row -> row.selectedProperty().get())
                .count();
        selectedLabel.setText(count + (count == 1 ? " item selected" : " items selected"));
    }

    /**
     * Updates the lower-left status text.
     *
     * @param state display state
     */
    private void setState(final String state) {
        stateLabel.setText(state);
    }

    @FXML
    private void onExit() {
        Platform.exit();
    }

    @FXML
    private void onSettings() {
        setState("Settings selected - implementation scheduled for a later GUI batch");
    }

    @FXML
    private void onSave() {
        setState("Save selected - implementation scheduled for a later GUI batch");
    }

    @FXML
    private void onRegister() {
        setState("Register selected - implementation scheduled for a later GUI batch");
    }

    @FXML
    private void onRegisterFromFile() {
        setState("Register from File selected - implementation scheduled for a later GUI batch");
    }

    @FXML
    private void onUnregister() {
        setState("Unregister selected - implementation scheduled for a later GUI batch");
    }

    @FXML
    private void onEnable() {
        setState("Enable selected - implementation scheduled for a later GUI batch");
    }

    @FXML
    private void onDisable() {
        setState("Disable selected - implementation scheduled for a later GUI batch");
    }

    @FXML
    private void onExecute() {
        setState("Execute selected - implementation scheduled for a later GUI batch");
    }

    @FXML
    private void onDryRun() {
        setState("Dry-run selected - implementation scheduled for a later GUI batch");
    }

    @FXML
    private void onPluginDetail() {
        setState("Plugin Detail selected - implementation scheduled for a later GUI batch");
    }

    @FXML
    private void onLogs() {
        setState("Logs selected - implementation scheduled for a later GUI batch");
    }

    @FXML
    private void onHelp() {
        setState("Help selected - implementation scheduled for a later GUI batch");
    }

    @FXML
    private void onAbout() {
        setState("About selected - implementation scheduled for a later GUI batch");
    }
}
