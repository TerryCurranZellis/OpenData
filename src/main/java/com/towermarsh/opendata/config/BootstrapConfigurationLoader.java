/*
 * Filename: BootstrapConfigurationLoader.java
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import com.towermarsh.opendata.config.model.BootstrapConfig;

/**
 * Loads application bootstrap configuration from the classpath.
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public final class BootstrapConfigurationLoader {

    private static final String RESOURCE =
            "config/application.properties";

    private final ClassLoader classLoader;

    public BootstrapConfigurationLoader() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public BootstrapConfigurationLoader(final ClassLoader classLoader) {
        this.classLoader = Objects.requireNonNull(classLoader, "classLoader");
    }

    public BootstrapConfig load() {
        try (InputStream input = classLoader.getResourceAsStream(RESOURCE)) {
            if (input == null) {
                throw new PluginDefinitionException(
                        "Bootstrap resource was not found: " + RESOURCE);
            }

            final Properties properties = new Properties();
            properties.load(new InputStreamReader(
                    input,
                    StandardCharsets.UTF_8));

            final Map<String, String> values = new LinkedHashMap<>();
            properties.stringPropertyNames().forEach(
                    name -> values.put(name, properties.getProperty(name).trim()));

            return new BootstrapConfig(
                    require(values, "application.name"),
                    require(values, "application.environment"),
                    Path.of(require(values, "application.working-directory")),
                    Path.of(require(values, "application.archive-directory")),
                    Path.of(require(values, "application.failed-directory")),
                    values);
        } catch (IOException exception) {
            throw new PluginDefinitionException(
                    "Unable to load bootstrap configuration.",
                    exception);
        }
    }

    private static String require(
            final Map<String, String> values,
            final String key) {

        final String value = values.get(key);
        if (value == null || value.isBlank()) {
            throw new PluginDefinitionException(
                    "Required bootstrap property is missing: " + key);
        }
        return value;
    }
}
