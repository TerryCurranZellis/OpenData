/*
 * Filename: PluginDefinitionLoader.java
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

import com.towermarsh.opendata.config.model.PluginDefinition;

/**
 * Loads a structured plugin definition from a storage-specific representation.
 *
 * @author Terry Curran
 * @version 21 Jul 2026
 */
public interface PluginDefinitionLoader {

    /**
     * Loads a plugin definition.
     *
     * @param pluginId selected plugin id
     * @param overrides optional merged override values
     * @return structured plugin definition
     */
    PluginDefinition load(String pluginId, Map<String, String> overrides);
}
