/*
 * Filename: package-info.java
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

/**
 * Bootstrap loading, plugin properties parsing and configuration validation.
 *
 * <p>Phase 1 uses a properties file for each plugin but immediately converts it
 * into structured immutable Java records. Plugins must consume
 * {@link com.towermarsh.opendata.config.ApplicationConfig} and
 * {@link com.towermarsh.opendata.config.model.PluginDefinition}; they should
 * not read raw {@link java.util.Properties} instances.</p>
 *
 * <p>This boundary permits a later database and JSON implementation without
 * changing plugin interfaces.</p>
 */
package com.towermarsh.opendata.config;
