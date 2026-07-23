/*
 * Filename: ApplicationConfigurationService.java
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

import java.util.Map;
import java.util.Objects;

import com.towermarsh.opendata.cli.CommandLineArguments;
import com.towermarsh.opendata.config.model.BootstrapConfig;
import com.towermarsh.opendata.config.model.PluginDefinition;

/**
 * Creates the Phase 1 {@link ApplicationConfig}.
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public final class ApplicationConfigurationService {

    private final BootstrapConfig bootstrapConfig;
    private final PluginDefinitionLoader pluginDefinitionLoader;

    /**
     * Configure the application service
     * @param bootstrapConfig
     * @param pluginDefinitionLoader 
     */
    public ApplicationConfigurationService(
            final BootstrapConfig bootstrapConfig,
            final PluginDefinitionLoader pluginDefinitionLoader) {

        this.bootstrapConfig = Objects.requireNonNull(
                bootstrapConfig,
                "bootstrapConfig");
        this.pluginDefinitionLoader = Objects.requireNonNull(
                pluginDefinitionLoader,
                "pluginDefinitionLoader");
    }

    /**
     * Resolves structured configuration for one command invocation.
     *
     * @param arguments parsed command-line arguments
     * @param runtimeOverrides invocation-only values
     * @return immutable application configuration
     */
    public ApplicationConfig resolve(
            final CommandLineArguments arguments,
            final Map<String, String> runtimeOverrides) {

        final var pluginId = arguments.pluginId()
                .orElseThrow(() -> new PluginDefinitionException(
                        "A plugin is required for an execution request."));

        final var plugin = pluginDefinitionLoader.load(
                pluginId,
                runtimeOverrides);

        return new ApplicationConfig(
                bootstrapConfig,
                plugin,
                runtimeOverrides,
                arguments.overrideFile(),
                arguments.dryRun(),
                arguments.verbose());
    }
}
