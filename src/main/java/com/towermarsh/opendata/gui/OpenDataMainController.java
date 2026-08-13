/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import static com.towermarsh.opendata.util.ExceptionMessages.rootCauseMessage;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;

/**
 * Controller for {@code OpenDataMainView.fxml}.
 *
 * <p>Batch 3 connects the plugin table to the persistent plugin registry and
 * run audit through {@link PluginTableDataLoader}. Database I/O is performed by
 * a JavaFX {@link Task}; the JavaFX application thread is used only to update
 * controls after the load succeeds or fails.</p>
 *
 * <p>Administration, execution and dialog actions remain placeholders until
 * their later GUI batches.</p>
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public final class OpenDataMainController {

    private static final Logger LOGGER = Logger.getLogger(OpenDataMainController.class.getName());

    private final PluginTableDataLoader pluginDataLoader;
    private Task<List<PluginTableEntry>> pluginLoadTask;

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

    @FXML
    private Label tablePlaceholderLabel;

    /**
     * Creates the FXML controller using the production plugin-data loader.
     */
    public OpenDataMainController() {
        this(new PluginTableDataLoader());
    }

    /**
     * Creates the controller with an explicit loader for focused tests.
     *
     * @param pluginDataLoader plugin-table data loader
     */
    OpenDataMainController(final PluginTableDataLoader pluginDataLoader) {
        this.pluginDataLoader = Objects.requireNonNull(pluginDataLoader, "pluginDataLoader");
    }

    /**
     * Configures the main page once the FXML has been loaded.
     */
    @FXML
    private void initialize() {
        pluginTable.setEditable(true);
        pluginTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        selectedColumn.setCellValueFactory(data -> data.getValue().selectedProperty());
        selectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectedColumn));
        pluginIdColumn.setCellValueFactory(data -> data.getValue().pluginIdProperty());
        descriptionColumn.setCellValueFactory(data -> data.getValue().descriptionProperty());
        enabledColumn.setCellValueFactory(data -> data.getValue().enabledStateProperty());
        lastRunStatusColumn.setCellValueFactory(data -> data.getValue().lastRunStatusProperty());
        lastRunDateColumn.setCellValueFactory(data -> data.getValue().lastRunDateProperty());

        refreshPluginTable();
    }

    /**
     * Reloads the main table from the persistent plugin registry.
     *
     * <p>The method is package-visible so later GUI administration batches can
     * reuse the same refresh boundary after a successful state-changing
     * operation.</p>
     */
    void refreshPluginTable() {
        if (pluginLoadTask != null && pluginLoadTask.isRunning()) {
            pluginLoadTask.cancel();
        }

        setState("Loading plugin details...");
        tablePlaceholderLabel.setText("Loading plugin details...");
        pluginTable.setDisable(true);
        pluginTable.getItems().clear();
        updateSelectionCount();

        final var task = new Task<List<PluginTableEntry>>() {
            @Override
            protected List<PluginTableEntry> call() {
                return pluginDataLoader.load();
            }
        };
        pluginLoadTask = task;

        task.setOnSucceeded(event -> {
            if (task != pluginLoadTask) {
                return;
            }
            replaceRows(task.getValue());
            tablePlaceholderLabel.setText("No plugins registered");
            pluginTable.setDisable(false);
            setState("Ready");
        });

        task.setOnFailed(event -> {
            if (task != pluginLoadTask) {
                return;
            }
            final var exception = task.getException();
            LOGGER.log(Level.SEVERE,
                    "Unable to load plugin details: {0}",
                    rootCauseMessage(exception));
            LOGGER.log(Level.FINE, "Plugin table loading failure details.", exception);
            pluginTable.getItems().clear();
            tablePlaceholderLabel.setText("Plugin details could not be loaded");
            pluginTable.setDisable(true);
            updateSelectionCount();
            setState("Unable to load plugin details");
        });

        final var worker = new Thread(task, "OpenData-GUI-PluginLoader");
        worker.setDaemon(true);
        worker.start();
    }

    private void replaceRows(final List<PluginTableEntry> entries) {
        final var rows = FXCollections.observableArrayList(
                entries.stream().map(PluginRow::from).toList());
        rows.forEach(row -> row.selectedProperty().addListener(
                (observable, oldValue, newValue) -> updateSelectionCount()));
        pluginTable.setItems(rows);
        updateSelectionCount();
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
