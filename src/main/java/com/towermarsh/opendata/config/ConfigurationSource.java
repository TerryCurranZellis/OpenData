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
     *
     */
    BUILT_IN_DEFAULT,

    /**
     *
     */
    APPLICATION_CLASSPATH,

    /**
     *
     */
    PLUGIN_CLASSPATH,

    /**
     *
     */
    OVERRIDE_FILE
}
