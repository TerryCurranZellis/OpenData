/*
 * Filename: ApplicationConfig.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The author may be contacted by email to the following address:
 *
 * terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.config;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.towermarsh.opendata.config.model.BootstrapConfig;
import com.towermarsh.opendata.config.model.PluginDefinition;

/**
 * Immutable configuration for one application execution.
 *
 * <p>
 * Phase 1 loads the {@link PluginDefinition} from a plugin properties file. A
 * future database-backed loader can provide the same record without changing
 * plugin code.</p>
 *
 * @param bootstrap application bootstrap configuration
 * @param plugin structured selected plugin definition
 * @param runtimeOverrides invocation-only override values
 * @param overrideFile optional properties override file
 * @param dryRun whether persistent pipeline changes are disabled
 * @param verbose whether verbose logging is requested
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public record ApplicationConfig(
        BootstrapConfig bootstrap,
        PluginDefinition plugin,
        Map<String, String> runtimeOverrides,
        Optional<Path> overrideFile,
        boolean dryRun,
        boolean verbose) {

    /**
     * validation for ApplicationConfig record
     *
     * @param bootstrap application bootstrap configuration
     * @param plugin structured selected plugin definition
     * @param runtimeOverrides invocation-only override values
     * @param overrideFile optional properties override file
     * @param dryRun whether persistent pipeline changes are disabled
     * @param verbose whether verbose logging is requested
     */
    public ApplicationConfig {
        Objects.requireNonNull(bootstrap, "bootstrap");
        Objects.requireNonNull(plugin, "plugin");
        runtimeOverrides = Map.copyOf(
                Objects.requireNonNull(runtimeOverrides, "runtimeOverrides"));
        overrideFile = overrideFile == null ? Optional.empty() : overrideFile;
    }
/**
 * get the plugin id
 * @return return the plugin id
 */
    public String pluginId() {
        return plugin.id();
    }
}
