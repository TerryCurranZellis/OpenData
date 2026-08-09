package com.towermarsh.opendata.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Controller for {@code OpenDataMainView.fxml}.
 *
 * <p>The current handlers are intentionally presentation-only. They update the
 * status bar but do not invoke OpenData processing logic. Real behaviour can be
 * connected later without changing the FXML layout.</p>
 */
public final class OpenDataMainController {

    @FXML
    private TableView<PluginRow> pluginTable;

    @FXML
    private TableColumn<PluginRow, String> idColumn;

    @FXML
    private TableColumn<PluginRow, String> nameColumn;

    @FXML
    private TableColumn<PluginRow, String> categoryColumn;

    @FXML
    private TableColumn<PluginRow, String> statusColumn;

    @FXML
    private TableColumn<PluginRow, String> lastUpdatedColumn;

    @FXML
    private Label stateLabel;

    @FXML
    private Label selectedLabel;

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(data -> data.getValue().idProperty());
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        categoryColumn.setCellValueFactory(data -> data.getValue().categoryProperty());
        statusColumn.setCellValueFactory(data -> data.getValue().statusProperty());
        lastUpdatedColumn.setCellValueFactory(data -> data.getValue().lastUpdatedProperty());

        pluginTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        pluginTable.setItems(FXCollections.observableArrayList(
                new PluginRow("ofgem", "Ofgem Energy Price Cap", "Energy", "Enabled", "08 Aug 2026 15:56"),
                new PluginRow("openmeteo", "Open-Meteo Weather History", "Weather", "Enabled", "08 Aug 2026 15:54"),
                new PluginRow("octopus", "Octopus Energy Statements", "Energy", "Registered", "07 Aug 2026 18:20")));

        pluginTable.getSelectionModel().getSelectedItems().addListener(
                (javafx.collections.ListChangeListener<PluginRow>) change -> updateSelectionCount());

        updateSelectionCount();
        setState("Ready");
    }

    private void updateSelectionCount() {
        final int count = pluginTable.getSelectionModel().getSelectedItems().size();
        selectedLabel.setText(count + (count == 1 ? " item selected" : " items selected"));
    }

    private void setState(final String state) {
        stateLabel.setText(state);
    }

    @FXML
    private void onExit() {
        Platform.exit();
    }

    @FXML
    private void onPreferences() {
        setState("Preferences selected");
    }

    @FXML
    private void onSave() {
        setState("Save selected");
    }

    @FXML
    private void onRegister() {
        setState("Register selected");
    }

    @FXML
    private void onRegisterFromFile() {
        setState("Register from File selected");
    }

    @FXML
    private void onUnregisterAll() {
        setState("Unregister All selected");
    }

    @FXML
    private void onUnregisterSelected() {
        setState("Unregister Selected selected");
    }

    @FXML
    private void onEnableAll() {
        setState("Enable All selected");
    }

    @FXML
    private void onEnableSelected() {
        setState("Enable Selected selected");
    }

    @FXML
    private void onDisableAll() {
        setState("Disable All selected");
    }

    @FXML
    private void onDisableSelected() {
        setState("Disable Selected selected");
    }

    @FXML
    private void onExecuteAll() {
        setState("Execute All selected");
    }

    @FXML
    private void onExecuteSelected() {
        setState("Execute Selected selected");
    }

    @FXML
    private void onDryRunAll() {
        setState("Dry-run All selected");
    }

    @FXML
    private void onDryRunSelected() {
        setState("Dry-run Selected selected");
    }

    @FXML
    private void onDocumentation() {
        setState("Documentation selected");
    }

    @FXML
    private void onAbout() {
        setState("About selected");
    }
}
