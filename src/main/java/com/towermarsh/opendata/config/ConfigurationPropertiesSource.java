/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

import java.util.Map;

/**
 * Loads application and plugin property sets from one backing store.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
public interface ConfigurationPropertiesSource {

    /**
     * Loads application property values.
     *
     * @return normalised application property values
     */
    Map<String, String> loadApplicationProperties();

    /**
     * Loads one plugin property file as normalised key/value pairs.
     *
     * @param pluginId plugin identifier
     * @return normalised plugin property values
     */
    Map<String, String> loadPluginProperties(String pluginId);
}
