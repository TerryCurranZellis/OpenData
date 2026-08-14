/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import static com.towermarsh.opendata.util.ExceptionMessages.rootCauseMessage;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.Window;

/**
 * Controller for {@code OpenDataMainView.fxml}.
 *
 * <p>Plugin reads and Batch 4 administration operations run behind focused
 * service adapters on JavaFX {@link Task}s. The controller owns only selection,
 * dialogs, status feedback and presentation refresh.</p>
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public final class OpenDataMainController {

    private static final Logger LOGGER = Logger.getLogger(OpenDataMainController.class.getName());

    private final PluginTableDataLoader pluginDataLoader;
    private final PluginAdministrationGateway pluginAdministration;
    private Task<List<PluginTableEntry>> pluginLoadTask;
    private Task<?> administrationTask;

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

    @FXML
    private MenuItem registerMenuItem;

    @FXML
    private MenuItem registerFromFileMenuItem;

    @FXML
    private MenuItem unregisterMenuItem;

    @FXML
    private MenuItem enableMenuItem;

    @FXML
    private MenuItem disableMenuItem;

    @FXML
    private Button registerButton;

    @FXML
    private Button unregisterButton;

    @FXML
    private Button enableButton;

    @FXML
    private Button disableButton;

    /**
     * Creates the FXML controller using production GUI services.
     */
    public OpenDataMainController() {
        this(new PluginTableDataLoader(), new PluginAdministrationGateway());
    }

    /**
     * Creates the controller with explicit services for focused tests.
     *
     * @param pluginDataLoader plugin-table data loader
     * @param pluginAdministration plugin administration adapter
     */
    OpenDataMainController(
            final PluginTableDataLoader pluginDataLoader,
            final PluginAdministrationGateway pluginAdministration) {
        this.pluginDataLoader = Objects.requireNonNull(pluginDataLoader, "pluginDataLoader");
        this.pluginAdministration = Objects.requireNonNull(
                pluginAdministration, "pluginAdministration");
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
     */
    void refreshPluginTable() {
        if (pluginLoadTask != null && pluginLoadTask.isRunning()) {
            pluginLoadTask.cancel();
        }

        setState("Loading plugin details...");
        tablePlaceholderLabel.setText("Loading plugin details...");
        setAdministrationActionsDisabled(true);
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
            setAdministrationActionsDisabled(false);
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
            setAdministrationActionsDisabled(false);
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

    private void updateSelectionCount() {
        final long count = pluginTable.getItems().stream()
                .filter(row -> row.selectedProperty().get())
                .count();
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
    private void onSettings() {
        setState("Settings selected - implementation scheduled for a later GUI batch");
    }

    @FXML
    private void onSave() {
        setState("Save selected - implementation scheduled for a later GUI batch");
    }

    /**
     * Scans the plugin configuration folder for valid definitions that are not
     * already registered, confirms the discoveries, and registers them.
     */
    @FXML
    private void onRegister() {
        runAdministrationTask(
                "Scanning plugin configuration folder...",
                pluginAdministration::discoverNewPlugins,
                candidates -> {
                    if (candidates.isEmpty()) {
                        setState("Ready");
                        OpenDataDialogs.information(
                                ownerWindow(),
                                "No new plugins found",
                                "No unregistered plugin properties files were found in the "
                                + "OpenData plugin configuration folder.");
                        return;
                    }
                    setState("Ready");
                    if (OpenDataDialogs.confirm(
                            ownerWindow(),
                            "Confirm Registration",
                            "Register discovered plugin" + pluralSuffix(candidates) + "?",
                            registrationSummary(candidates))) {
                        registerDiscoveredPlugins(candidates);
                    }
                },
                "Unable to scan plugin configuration folder");
    }

    /**
     * Selects a complete plugin properties file with JavaFX FileChooser and
     * registers that definition.
     */
    @FXML
    private void onRegisterFromFile() {
        OpenDataDialogs.choosePluginDefinitionFile(
                ownerWindow(), pluginAdministration.registrationDirectories())
                .ifPresent(file -> runAdministrationTask(
                "Registering plugin from " + file.getFileName() + "...",
                () -> pluginAdministration.registerFromFile(file),
                pluginId -> {
                    LOGGER.log(Level.INFO, "Registered plugin from file: {0}", pluginId);
                    refreshPluginTable();
                },
                "Unable to register plugin from file"));
    }

    /**
     * Unregisters all explicitly checked plugins after confirmation.
     */
    @FXML
    private void onUnregister() {
        final var pluginIds = selectedPluginIdsOrWarn();
        if (pluginIds.isEmpty()) {
            return;
        }
        if (!OpenDataDialogs.confirm(
                ownerWindow(),
                "Confirm Unregister",
                "Unregister selected plugin" + pluralSuffix(pluginIds) + "?",
                selectionSummary(pluginIds))) {
            return;
        }
        runAdministrationTask(
                "Unregistering selected plugin" + pluralSuffix(pluginIds) + "...",
                () -> pluginAdministration.unregister(pluginIds),
                completed -> {
                    LOGGER.log(Level.INFO, "Unregistered plugin(s): {0}", completed);
                    refreshPluginTable();
                },
                "Unable to unregister selected plugin" + pluralSuffix(pluginIds));
    }

    /**
     * Enables all explicitly checked plugins after confirmation.
     */
    @FXML
    private void onEnable() {
        changeEnabledState(true);
    }

    /**
     * Disables all explicitly checked plugins after confirmation.
     */
    @FXML
    private void onDisable() {
        changeEnabledState(false);
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

    private void registerDiscoveredPlugins(
            final List<PluginRegistrationCandidate> candidates) {
        runAdministrationTask(
                "Registering discovered plugin" + pluralSuffix(candidates) + "...",
                () -> pluginAdministration.registerDiscovered(candidates),
                completed -> {
                    LOGGER.log(Level.INFO, "Registered discovered plugin(s): {0}", completed);
                    refreshPluginTable();
                },
                "Unable to register discovered plugin" + pluralSuffix(candidates));
    }

    private void changeEnabledState(final boolean enabled) {
        final var pluginIds = selectedPluginIdsOrWarn();
        if (pluginIds.isEmpty()) {
            return;
        }
        final var action = enabled ? "Enable" : "Disable";
        if (!OpenDataDialogs.confirm(
                ownerWindow(),
                "Confirm " + action,
                action + " selected plugin" + pluralSuffix(pluginIds) + "?",
                selectionSummary(pluginIds))) {
            return;
        }
        runAdministrationTask(
                (enabled ? "Enabling" : "Disabling") + " selected plugin" + pluralSuffix(pluginIds) + "...",
                () -> pluginAdministration.setEnabled(pluginIds, enabled),
                completed -> {
                    LOGGER.log(Level.INFO, "{0}d plugin(s): {1}",
                            new Object[]{action, completed});
                    refreshPluginTable();
                },
                "Unable to " + action.toLowerCase() + " selected plugin"
                        + pluralSuffix(pluginIds));
    }

    private List<String> selectedPluginIdsOrWarn() {
        final var pluginIds = pluginTable.getItems().stream()
                .filter(row -> row.selectedProperty().get())
                .map(row -> row.pluginIdProperty().get())
                .toList();
        if (pluginIds.isEmpty()) {
            OpenDataDialogs.warning(
                    ownerWindow(),
                    "No plugin selected",
                    "Select one or more plugins using the Selected checkbox.");
        }
        return pluginIds;
    }

    private <T> void runAdministrationTask(
            final String workingState,
            final Callable<T> operation,
            final Consumer<T> onSucceeded,
            final String failureState) {
        Objects.requireNonNull(operation, "operation");
        Objects.requireNonNull(onSucceeded, "onSucceeded");

        if (administrationTask != null && administrationTask.isRunning()) {
            setState("An administration operation is already running");
            return;
        }

        setState(workingState);
        setAdministrationActionsDisabled(true);
        pluginTable.setDisable(true);

        final var task = new Task<T>() {
            @Override
            protected T call() throws Exception {
                return operation.call();
            }
        };
        administrationTask = task;

        task.setOnSucceeded(event -> {
            if (task != administrationTask) {
                return;
            }
            administrationTask = null;
            setAdministrationActionsDisabled(false);
            pluginTable.setDisable(false);
            onSucceeded.accept(task.getValue());
        });

        task.setOnFailed(event -> {
            if (task != administrationTask) {
                return;
            }
            administrationTask = null;
            final var exception = task.getException();
            final var message = rootCauseMessage(exception);
            LOGGER.log(Level.SEVERE, failureState + ": {0}", message);
            LOGGER.log(Level.FINE, "GUI plugin administration failure details.", exception);
            setAdministrationActionsDisabled(false);
            pluginTable.setDisable(false);
            setState(failureState);
            OpenDataDialogs.error(ownerWindow(), failureState, message);
        });

        final var worker = new Thread(task, "OpenData-GUI-PluginAdministration");
        worker.setDaemon(true);
        worker.start();
    }

    private void setAdministrationActionsDisabled(final boolean disabled) {
        registerMenuItem.setDisable(disabled);
        registerFromFileMenuItem.setDisable(disabled);
        unregisterMenuItem.setDisable(disabled);
        enableMenuItem.setDisable(disabled);
        disableMenuItem.setDisable(disabled);
        registerButton.setDisable(disabled);
        unregisterButton.setDisable(disabled);
        enableButton.setDisable(disabled);
        disableButton.setDisable(disabled);
    }

    private Window ownerWindow() {
        return pluginTable.getScene() == null ? null : pluginTable.getScene().getWindow();
    }

    private static String selectionSummary(final List<String> pluginIds) {
        return "Selected plugin" + pluralSuffix(pluginIds) + ":\n"
                + String.join(System.lineSeparator(), pluginIds);
    }

    private static String registrationSummary(
            final List<PluginRegistrationCandidate> candidates) {
        return candidates.stream()
                .map(candidate -> "%s — %s (%s)".formatted(
                candidate.pluginId(),
                candidate.displayName(),
                candidate.file().getFileName()))
                .collect(java.util.stream.Collectors.joining(System.lineSeparator()));
    }

    private static String pluralSuffix(final List<?> values) {
        return values.size() == 1 ? "" : "s";
    }
}
