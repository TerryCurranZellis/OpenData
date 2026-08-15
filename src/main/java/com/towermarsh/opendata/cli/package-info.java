/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Apache Commons CLI parsing and validation for plugin execution,
 * registration, removal, enablement, disablement and informational commands.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link CommandLineArgumentsProcessor} &mdash; Parses and validates the OpenData command line.</li>
 * <li>{@link CommandLineProcessingException} &mdash; Raised when command-line arguments are invalid.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link CommandLineArguments} &mdash; Immutable command-line arguments for one invocation.</li>
 * </ul>
 *
 * <h2>Enums</h2>
 * <ul>
 * <li>{@link PluginCommand} &mdash; Operation requested for the selected plugins.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
package com.towermarsh.opendata.cli;
