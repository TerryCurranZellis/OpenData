/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

/**
 * Validates resolved application and plugin configuration.
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
@FunctionalInterface
public interface ConfigurationValidator {

    /**
     * Validates configuration and throws a ConfigurationException when invalid.
     *
     * @param configuration resolved configuration
     */
    void validate(ApplicationConfig configuration);
}
