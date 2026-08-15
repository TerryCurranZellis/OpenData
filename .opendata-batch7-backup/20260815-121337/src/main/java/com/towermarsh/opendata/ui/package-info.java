/*
 * Copyright Â© 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Legacy desktop user-interface helpers retained during the JavaFX migration.
 *
 * <p>New graphical-interface code belongs under
 * {@code com.towermarsh.opendata.gui}. The Swing-based About dialog, image
 * loader and compatibility launcher are no longer used by the JavaFX GUI and
 * are marked for removal. The deprecated Swing startup splash remains only on
 * the legacy command-line run path until that compatibility behaviour is
 * reviewed in the final migration batch.</p>
 *
 * <ul>
 * <li>{@link com.towermarsh.opendata.ui.AboutDialog} - legacy Swing About dialog</li>
 * <li>{@link com.towermarsh.opendata.ui.GuiLauncher} - compatibility launcher</li>
 * <li>{@link com.towermarsh.opendata.app.ApplicationInfo} - application display metadata</li>
 * <li>{@link com.towermarsh.opendata.ui.StartupSplashScreen} - legacy Swing splash</li>
 * </ul>
 *
 * @version 3.1.0
 */
package com.towermarsh.opendata.ui;
