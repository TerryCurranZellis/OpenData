/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */

/**
 * Initialise step for the Octopus plugin.
 *
 * <p>Responsible for loading configuration and orchestrating the full
 * Extract → Transform → Load → Finalise pipeline.
 *
 * <ul>
 * <li>{@link OctopusConfiguration} – typed plugin configuration</li>
 * <li>{@link OctopusInitialise} – pipeline orchestrator</li>
 * </ul>
 */
package com.towermarsh.opendata.plugin.octopus.initialise;
