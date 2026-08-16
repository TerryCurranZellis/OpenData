/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * JavaFX presentation and lifecycle layer for the OpenData graphical interface.
 *
 * <p>
 * This package owns JavaFX startup, the startup splash, FXML views,
 * controllers, dialog presentation, plugin administration and live
 * execution-log presentation. Core OpenData services remain outside this
 * package and are integrated through explicit service boundaries; potentially
 * blocking backend work is performed away from the JavaFX application
 * thread.</p>
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link ApplicationSettingsGateway} &mdash; Resolves the effective
 * non-sensitive application settings shown by the JavaFX Settings/Preferences
 * dialog.</li>
 * <li>{@link ConfigurationDisplayMasker} &mdash; Converts configuration maps
 * into safe read-only GUI values.</li>
 * <li>{@link GuiLauncher} &mdash; Launches the OpenData JavaFX desktop
 * application.</li>
 * <li>{@link JavaFxLogHandler} &mdash; JUL handler that batches formatted log
 * text onto the JavaFX application thread.</li>
 * <li>{@link LogViewerService} &mdash; Reads the current OpenData rotating log
 * for the JavaFX log viewer.</li>
 * <li>{@link OpenDataAboutApplication} &mdash; Minimal JavaFX application used
 * by the command-line {@code --about} route.</li>
 * <li>{@link OpenDataDialogs} &mdash; Common JavaFX dialogs used by the
 * OpenData main window.</li>
 * <li>{@link OpenDataExecutionWindow} &mdash; Modal JavaFX window that displays
 * live Execute or Dry-run JUL output.</li>
 * <li>{@link OpenDataGuiApplication} &mdash; JavaFX application for the
 * OpenData graphical interface.</li>
 * <li>{@link OpenDataHelpContent} &mdash; Loads the built-in JavaFX help
 * overview used when compiled Windows Help is unavailable.</li>
 * <li>{@link OpenDataHelpLauncher} &mdash; Opens the compiled Windows HTML Help
 * file when it is available and falls back to the built-in JavaFX help viewer
 * otherwise.</li>
 * <li>{@link OpenDataInformationDialogs} &mdash; Custom JavaFX information
 * dialogs used by the OpenData desktop interface.</li>
 * <li>{@link OpenDataMainController} &mdash; Controller for
 * {@code OpenDataMainView.fxml}.</li>
 * <li>{@link OpenDataSplashScreen} &mdash; JavaFX startup splash used by the
 * OpenData desktop interface.</li>
 * <li>{@link PluginAdministrationGateway} &mdash; Resource-owning adapter for
 * JavaFX plugin administration operations.</li>
 * <li>{@link PluginDetailGateway} &mdash; Loads one registered plugin's stored
 * configuration for the JavaFX detail dialog.</li>
 * <li>{@link PluginExecutionGateway} &mdash; Resource-owning adapter for JavaFX
 * plugin execution and dry-run operations.</li>
 * <li>{@link PluginRow} &mdash; Presentation model for one plugin displayed in
 * the JavaFX main-window table.</li>
 * <li>{@link PluginTableDataLoader} &mdash; Opens the bootstrap database
 * resources needed for one GUI plugin-table load.</li>
 * <li>{@link PluginTableDataService} &mdash; Loads the read-only plugin
 * information required by the JavaFX main table.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link ConfigurationDisplayEntry} &mdash; One read-only property/value
 * row displayed by a JavaFX information dialog.</li>
 * <li>{@link LogSnapshot} &mdash; Snapshot of the application log shown in the
 * JavaFX log viewer.</li>
 * <li>{@link PluginRegistrationCandidate} &mdash; Validated, not-yet-registered
 * plugin definition discovered by the GUI.</li>
 * <li>{@link PluginTableEntry} &mdash; Read-only data required to populate one
 * row in the JavaFX plugin table.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
package com.towermarsh.opendata.gui;
