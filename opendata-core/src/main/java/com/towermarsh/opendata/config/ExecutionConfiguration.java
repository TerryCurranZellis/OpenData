/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import com.towermarsh.opendata.foundation.ValidationRules;
import java.time.Duration;

/** 
 * Bounded plugin executor settings.
 * @param maxParallelPlugins maximum number of plugins to execute concurrently
 * @param shutdownTimeout maximum time to wait for plugin shutdown
  *
 * @author Terry Curran
 * @since 2.1.0
 * @version 4.1.0
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
