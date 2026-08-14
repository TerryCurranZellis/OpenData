/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * JavaFX presentation and lifecycle layer for the OpenData graphical interface.
 *
 * <p>The package owns JavaFX startup, the startup splash, views, controllers
 * and GUI presentation models. Core OpenData services remain outside this
 * package and are integrated through explicit service boundaries. Potentially
 * blocking backend work is performed away from the JavaFX application thread.
 * Batch 5 adds read-only configuration, log, Help and About presentation with
 * sensitive configuration values masked before display.</p>
 *
 * <p>Package Content</p>
 * <ul>
 * <li>{@link ApplicationSettingsGateway}</li>
 * <li>{@link ConfigurationDisplayEntry}</li>
 * <li>{@link ConfigurationDisplayMasker}</li>
 * <li>{@link GuiLauncher}</li>
 * <li>{@link LogSnapshot}</li>
 * <li>{@link LogViewerService}</li>
 * <li>{@link OpenDataAboutApplication}</li>
 * <li>{@link OpenDataDialogs}</li>
 * <li>{@link OpenDataGuiApplication}</li>
 * <li>{@link OpenDataHelpContent}</li>
 * <li>{@link OpenDataInformationDialogs}</li>
 * <li>{@link OpenDataMainController}</li>
 * <li>{@link OpenDataSplashScreen}</li>
 * <li>{@link PluginAdministrationGateway}</li>
 * <li>{@link PluginDetailGateway}</li>
 * <li>{@link PluginRegistrationCandidate}</li>
 * <li>{@link PluginRow}</li>
 * <li>{@link PluginTableDataLoader}</li>
 * <li>{@link PluginTableDataService}</li>
 * <li>{@link PluginTableEntry}</li>
 * </ul>
 * @since 3.0.0
 * @version 3.1.0
 */
package com.towermarsh.opendata.gui;
