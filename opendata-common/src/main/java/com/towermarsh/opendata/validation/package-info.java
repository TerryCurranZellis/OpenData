/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Shared validation contracts, typed plugin-property parsing, and reusable
 * validation rules.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link PluginPropertyValues} &mdash; Provides consistent typed access to resolved plugin property values.</li>
 * <li>{@link SqlIdentifiers} &mdash; Validates and quotes SQL Server identifiers supplied through configuration.</li>
 * <li>{@link ValidationRules} &mdash; Reusable validation rules for configuration and transformed records.</li>
 * </ul>
 *
 * <h2>Interfaces</h2>
 * <ul>
 * <li>{@link ValueParser} &mdash; Converts a textual configuration value into a typed value.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
package com.towermarsh.opendata.validation;
