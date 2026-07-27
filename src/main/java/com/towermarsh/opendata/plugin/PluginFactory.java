/*
 * Filename: PluginFactory.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

/**
 * Creates a fresh plugin instance for one task.
 *
 *
 * @author Terry Curran
 * @version 17 July 2026
 */
@FunctionalInterface
public interface PluginFactory {

    /**
     * Creates a fresh plugin instance for one resolved plugin definition.
     *
     * @param plugin resolved plugin metadata and definition
     * @return plugin instance ready for execution
     *
     */
    OpenDataPlugin create(ResolvedPlugin plugin);
}
