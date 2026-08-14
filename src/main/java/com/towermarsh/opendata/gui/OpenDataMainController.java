/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.gui;

import static com.towermarsh.opendata.util.ExceptionMessages.rootCauseMessage;

import com.towermarsh.opendata.logging.LoggingManager;
import com.towermarsh.opendata.plugin.PluginExecutionSummary;
import com.towermarsh.opendata.ui.ApplicationInfo;
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
 * <p>Plugin reads, administration, information and execution operations run
 * behind focused service adapters on JavaFX {@link Task}s. The controller owns
 * only selection snapshots, dialogs, status feedback, live-log attachment and
 * presentation refresh.</p>
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public final class OpenDataMainController {

    private static final Logger LOGGER = Logger.getLogger(OpenDataMainController.class.getName());

    private final PluginTableDataLoader pluginDataLoader;
    private final PluginAdministrationGateway pluginAdministration;
    private final PluginDetailGateway pluginDetailGateway;
    private final ApplicationSettingsGateway settingsGateway;
    private final LogViewerService logViewerService;
    private final PluginExecutionGateway pluginExecutionGateway;
    private Task<List<PluginTableEntry>> pluginLoadTask;
    private Task<?> administrationTask;
    private Task<?> informationTask;
    private Task<PluginExecutionSummary> executionTask;

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
    private MenuItem executeMenuItem;

    @FXML
    private MenuItem dryRunMenuItem;

    @FXML
    private Button registerButton;

    @FXML
    private Button unregisterButton;

    @FXML
    private Button enableButton;

    @FXML
    private Button disableButton;

    @FXML
    private Button executeButton;

    @FXML
    private Button dryRunButton;

    /**
     * Creates the FXML controller using production GUI services.
     */
    public OpenDataMainController() {
        this(
                new PluginTableDataLoader(),
                new PluginAdministrationGateway(),
                new PluginDetailGateway(),
                new ApplicationSettingsGateway(),
                new LogViewerService(),
                new PluginExecutionGateway());
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
        this(
                pluginDataLoader,
                pluginAdministration,
                new PluginDetailGateway(),
                new ApplicationSettingsGateway(),
                new LogViewerService(),
                new PluginExecutionGateway());
    }

    /**
     * Creates the controller with explicit read and administration services.
     *
     * @param pluginDataLoader main table loader
     * @param pluginAdministration plugin administration adapter
     * @param pluginDetailGateway plugin detail reader
     * @param settingsGateway application settings reader
     * @param logViewerService application log reader
     * @param pluginExecutionGateway plugin Execute/Dry-run adapter
     */
    OpenDataMainController(
            final PluginTableDataLoader pluginDataLoader,
            final PluginAdministrationGateway pluginAdministration,
            final PluginDetailGateway pluginDetailGateway,
            final ApplicationSettingsGateway settingsGateway,
            final LogViewerService logViewerService,
            final PluginExecutionGateway pluginExecutionGateway) {
        this.pluginDataLoader = Objects.requireNonNull(pluginDataLoader, "pluginDataLoader");
        this.pluginAdministration = Objects.requireNonNull(
                pluginAdministration, "pluginAdministration");
        this.pluginDetailGateway = Objects.requireNonNull(
                pluginDetailGateway, "pluginDetailGateway");
        this.settingsGateway = Objects.requireNonNull(settingsGateway, "settingsGateway");
        this.logViewerService = Objects.requireNonNull(logViewerService, "logViewerService");
        this.pluginExecutionGateway = Objects.requireNonNull(
                pluginExecutionGateway, "pluginExecutionGateway");
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
        setMutatingActionsDisabled(true);
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
            setMutatingActionsDisabled(false);
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
            setMutatingActionsDisabled(false);
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
        runInformationTask(
                "Loading application settings...",
                settingsGateway::load,
                entries -> {
                    setState("Ready");
                    OpenDataInformationDialogs.showProperties(
                            ownerWindow(),
                            "OpenData Settings",
                            "Effective application settings (read-only)",
                            entries);
                },
                "Unable to load application settings");
    }

    @FXML
    private void onSave() {
        setState("Ready");
        OpenDataDialogs.information(
                ownerWindow(),
                "Settings are read-only",
                "Batch 5 displays the effective settings without editing them. "
                + "No Save action is required for the current specification.");
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
        startExecution(false);
    }

    @FXML
    private void onDryRun() {
        startExecution(true);
    }

    private void startExecution(final boolean dryRun) {
        final var pluginIds = selectedPluginIdsOrWarn();
        if (pluginIds.isEmpty()) {
            return;
        }
        if (pluginLoadTask != null && pluginLoadTask.isRunning()) {
            setState("Plugin details are still loading");
            return;
        }
        if (executionTask != null && executionTask.isRunning()) {
            setState("A plugin execution is already running");
            return;
        }
        if (administrationTask != null && administrationTask.isRunning()) {
            setState("An administration operation is already running");
            return;
        }
        if (informationTask != null && informationTask.isRunning()) {
            setState("An information operation is already running");
            return;
        }

        final var operationName = dryRun ? "Dry-run" : "Execute";
        final var confirmation = selectionSummary(pluginIds)
                + System.lineSeparator() + System.lineSeparator()
                + (dryRun
                        ? "Dry-run performs extraction and transformation but does not write "
                        + "plugin data or generic run-audit rows."
                        : "Execute may insert or update persistent plugin data.");
        if (!OpenDataDialogs.confirm(
                ownerWindow(),
                "Confirm " + operationName,
                operationName + " selected plugin" + pluralSuffix(pluginIds) + "?",
                confirmation)) {
            return;
        }

        final var executionWindow = new OpenDataExecutionWindow(
                ownerWindow(), operationName, pluginIds);
        final var liveLogHandler = new JavaFxLogHandler(executionWindow::appendLog);
        final var applicationLogger = LoggingManager.getLogger();
        applicationLogger.addHandler(liveLogHandler);

        setState((dryRun ? "Dry-running" : "Executing")
                + " selected plugin" + pluralSuffix(pluginIds) + "...");
        setMutatingActionsDisabled(true);
        pluginTable.setDisable(true);
        executionWindow.show();

        final var task = new Task<PluginExecutionSummary>() {
            @Override
            protected PluginExecutionSummary call() throws Exception {
                return pluginExecutionGateway.execute(pluginIds, dryRun);
            }
        };
        executionTask = task;

        task.setOnSucceeded(event -> {
            if (task != executionTask) {
                return;
            }
            executionTask = null;
            liveLogHandler.flush();
            applicationLogger.removeHandler(liveLogHandler);
            liveLogHandler.close();

            final var summary = task.getValue();
            executionWindow.complete(summary);
            setState(summary.allSuccessful()
                    ? operationName + " completed successfully"
                    : operationName + " completed with failures");
            setMutatingActionsDisabled(false);
            pluginTable.setDisable(false);
            refreshPluginTable();
        });

        task.setOnFailed(event -> {
            if (task != executionTask) {
                return;
            }
            executionTask = null;
            final var exception = task.getException();
            final var message = rootCauseMessage(exception);
            LOGGER.log(Level.SEVERE, operationName + " failed: {0}", message);
            LOGGER.log(Level.FINE, "GUI plugin execution failure details.", exception);
            liveLogHandler.flush();
            applicationLogger.removeHandler(liveLogHandler);
            liveLogHandler.close();

            executionWindow.fail(message);
            setState(operationName + " failed");
            setMutatingActionsDisabled(false);
            pluginTable.setDisable(false);
            refreshPluginTable();
        });

        final var worker = new Thread(task, "OpenData-GUI-PluginExecution");
        worker.setDaemon(true);
        worker.start();
    }

    @FXML
    private void onPluginDetail() {
        final var pluginId = singleSelectedPluginIdOrWarn();
        if (pluginId == null) {
            return;
        }
        runInformationTask(
                "Loading plugin details for " + pluginId + "...",
                () -> pluginDetailGateway.load(pluginId),
                entries -> {
                    setState("Ready");
                    OpenDataInformationDialogs.showProperties(
                            ownerWindow(),
                            "Plugin Detail — " + pluginId,
                            "Stored configuration for " + pluginId,
                            entries);
                },
                "Unable to load plugin details");
    }

    @FXML
    private void onLogs() {
        runInformationTask(
                "Loading application log...",
                logViewerService::load,
                snapshot -> {
                    setState("Ready");
                    OpenDataInformationDialogs.showLog(ownerWindow(), snapshot);
                },
                "Unable to load application log");
    }

    @FXML
    private void onHelp() {
        setState("Ready");
        OpenDataInformationDialogs.showHelp(ownerWindow());
    }

    @FXML
    private void onAbout() {
        setState("Ready");
        OpenDataInformationDialogs.showAbout(ownerWindow(), ApplicationInfo.current());
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

    private String singleSelectedPluginIdOrWarn() {
        final var pluginIds = selectedPluginIds();
        if (pluginIds.isEmpty()) {
            OpenDataDialogs.warning(
                    ownerWindow(),
                    "No plugin selected",
                    "Select one plugin using the Selected checkbox.");
            return null;
        }
        if (pluginIds.size() > 1) {
            OpenDataDialogs.warning(
                    ownerWindow(),
                    "Select one plugin",
                    "Plugin Detail can display one plugin at a time.");
            return null;
        }
        return pluginIds.get(0);
    }

    private List<String> selectedPluginIds() {
        return pluginTable.getItems().stream()
                .filter(row -> row.selectedProperty().get())
                .map(row -> row.pluginIdProperty().get())
                .toList();
    }

    private List<String> selectedPluginIdsOrWarn() {
        final var pluginIds = selectedPluginIds();
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

        if (pluginLoadTask != null && pluginLoadTask.isRunning()) {
            setState("Plugin details are still loading");
            return;
        }
        if (administrationTask != null && administrationTask.isRunning()) {
            setState("An administration operation is already running");
            return;
        }
        if (executionTask != null && executionTask.isRunning()) {
            setState("A plugin execution is already running");
            return;
        }
        if (informationTask != null && informationTask.isRunning()) {
            setState("An information operation is already running");
            return;
        }

        setState(workingState);
        setMutatingActionsDisabled(true);
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
            setMutatingActionsDisabled(false);
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
            setMutatingActionsDisabled(false);
            pluginTable.setDisable(false);
            setState(failureState);
            OpenDataDialogs.error(ownerWindow(), failureState, message);
        });

        final var worker = new Thread(task, "OpenData-GUI-PluginAdministration");
        worker.setDaemon(true);
        worker.start();
    }

    private <T> void runInformationTask(
            final String workingState,
            final Callable<T> operation,
            final Consumer<T> onSucceeded,
            final String failureState) {
        Objects.requireNonNull(operation, "operation");
        Objects.requireNonNull(onSucceeded, "onSucceeded");

        if (pluginLoadTask != null && pluginLoadTask.isRunning()) {
            setState("Plugin details are still loading");
            return;
        }
        if (informationTask != null && informationTask.isRunning()) {
            setState("An information operation is already running");
            return;
        }
        if (administrationTask != null && administrationTask.isRunning()) {
            setState("An administration operation is already running");
            return;
        }
        if (executionTask != null && executionTask.isRunning()) {
            setState("A plugin execution is already running");
            return;
        }

        setState(workingState);
        final var task = new Task<T>() {
            @Override
            protected T call() throws Exception {
                return operation.call();
            }
        };
        informationTask = task;

        task.setOnSucceeded(event -> {
            if (task != informationTask) {
                return;
            }
            informationTask = null;
            onSucceeded.accept(task.getValue());
        });

        task.setOnFailed(event -> {
            if (task != informationTask) {
                return;
            }
            informationTask = null;
            final var exception = task.getException();
            final var message = rootCauseMessage(exception);
            LOGGER.log(Level.SEVERE, failureState + ": {0}", message);
            LOGGER.log(Level.FINE, "GUI information operation failure details.", exception);
            setState(failureState);
            OpenDataDialogs.error(ownerWindow(), failureState, message);
        });

        final var worker = new Thread(task, "OpenData-GUI-Information");
        worker.setDaemon(true);
        worker.start();
    }

    private void setMutatingActionsDisabled(final boolean disabled) {
        setAdministrationActionsDisabled(disabled);
        executeMenuItem.setDisable(disabled);
        dryRunMenuItem.setDisable(disabled);
        executeButton.setDisable(disabled);
        dryRunButton.setDisable(disabled);
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
