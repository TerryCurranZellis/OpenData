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
 * sensitive configuration values masked before display. Batch 6 adds
 * confirmed Execute/Dry-run orchestration, live JUL streaming and a modal
 * execution window whose Close action remains disabled until processing has
 * completed.</p>
 *
 * @since 3.0.0
 * @version 3.1.0
 */
package com.towermarsh.opendata.gui;
