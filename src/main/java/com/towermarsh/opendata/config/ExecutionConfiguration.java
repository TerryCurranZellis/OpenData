/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import java.time.Duration;
import java.util.Objects;

/** 
 * Bounded plugin executor settings.
 * @param maxParallelPlugins maximum number of plugins to execute concurrently
 * @param shutdownTimeout maximum time to wait for plugin shutdown
  *
 * @author Terry Curran
 * @version 1.0.0
 */
public record ExecutionConfiguration(int maxParallelPlugins, Duration shutdownTimeout) {
    /** 
     * Validates and normalises record components. 
     */
    public ExecutionConfiguration {
        Objects.requireNonNull(shutdownTimeout, "shutdownTimeout");
        if (maxParallelPlugins < 1 || maxParallelPlugins > 64) {
            throw new OpenDataConfigurationException("execution.max-parallel-plugins must be between 1 and 64.");
        }
        if (shutdownTimeout.isNegative() || shutdownTimeout.isZero()) {
            throw new OpenDataConfigurationException("execution.shutdown-timeout-seconds must be positive.");
        }
    }
}
