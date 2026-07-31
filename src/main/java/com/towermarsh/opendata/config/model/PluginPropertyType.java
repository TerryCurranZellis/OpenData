/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.config.model;

/**
 * Declared type of a plugin-specific configuration property.
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
public enum PluginPropertyType {

    /**
     * String
     */
    STRING,

    /**
     * Integer value
     */
    INTEGER,

    /**
     * Long value
     */
    LONG,

    /**
     * Boolean
     */
    BOOLEAN,

    /**
     * decimal (real) number
     */
    DECIMAL,

    /**
     * whether we hold for a few seconds or a few hours
     */
    DURATION,

    /**
     * path
     */
    PATH,

    /**
     * URI
     */
    URI
}
