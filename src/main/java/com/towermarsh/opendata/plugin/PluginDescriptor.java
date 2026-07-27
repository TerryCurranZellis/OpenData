/*
 * Filename: PluginDescriptor.java
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
package com.towermarsh.opendata.plugin;

import java.util.Objects;

/**
 * Immutable metadata describing an installed properties-based plugin.
 *
 * @param id stable command-line plugin identifier
 * @param displayName human-readable plugin name
 * @param description plugin description
 * @param implementationClass configured implementation class
 * @param enabled whether the plugin may be executed
 * @param configurationVersion plugin configuration version
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public record PluginDescriptor(
        String id,
        String displayName,
        String description,
        String implementationClass,
        boolean enabled,
        int configurationVersion) {

    /** Validates and normalises record components. */
    public PluginDescriptor {
        id = requireText(id, "id");
        displayName = requireText(displayName, "displayName");
        description = description == null ? "" : description.trim();
        implementationClass = requireText(
                implementationClass,
                "implementationClass");

        if (configurationVersion < 1) {
            throw new IllegalArgumentException(
                    "configurationVersion must be at least 1.");
        }
    }

    /**
     * Returns a required non-blank text value.
     *
     * @param value value to validate
     * @param fieldName field name for error reporting
     * @return trimmed text value
     */
    private static String requireText(
            final String value,
            final String fieldName) {

        Objects.requireNonNull(value, fieldName);
        final String result = value.trim();
        if (result.isEmpty()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank.");
        }
        return result;
    }
}
