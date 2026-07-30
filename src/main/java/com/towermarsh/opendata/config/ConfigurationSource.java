/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config;

/**
 * Records where a resolved property value originated.
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public enum ConfigurationSource {

    /**
     * built in configuration
     */
    BUILT_IN_DEFAULT,

    /**
     * Application configuration
     */
    APPLICATION_CLASSPATH,

    /**
     * Plugin configuration
     */
    PLUGIN_CLASSPATH,

    /**
     * override configuration
     */
    OVERRIDE_FILE
}
