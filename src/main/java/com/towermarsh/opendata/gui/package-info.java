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
 * blocking backend work is performed away from the JavaFX application thread.</p>
 *
 * @since 3.0.0
 * @version 3.1.0
 */
package com.towermarsh.opendata.gui;
