/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

/**
 * A configuration value together with its source.
 *
 * @param value resolved text value
 * @param source source that supplied the value
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public record ResolvedConfigurationValue(
        String value,
        ConfigurationSource source) {
}
