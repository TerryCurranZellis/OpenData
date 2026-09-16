/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import com.towermarsh.opendata.validation.ValidationRules;
import java.time.Duration;

/** 
 * Bounded plugin executor settings.
 * @param maxParallelPlugins maximum number of plugins to execute concurrently
 * @param shutdownTimeout maximum time to wait for plugin shutdown
  *
 * @author Terry Curran
 * @version 2.1
 */
public record ExecutionConfiguration(int maxParallelPlugins, Duration shutdownTimeout) {
    /** 
     * Validates and normalises record components. 
     */
    public ExecutionConfiguration {
        try {
            maxParallelPlugins = ValidationRules.requireRange(
                    maxParallelPlugins, 1, 64, "execution.max-parallel-plugins");
            shutdownTimeout = ValidationRules.requirePositive(
                    shutdownTimeout, "execution.shutdown-timeout-seconds");
        } catch (IllegalArgumentException exception) {
            throw new OpenDataConfigurationException(exception.getMessage(), exception);
        }
    }
}
