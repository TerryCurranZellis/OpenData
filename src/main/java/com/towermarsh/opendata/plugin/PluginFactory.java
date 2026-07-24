/*
 * Filename: PluginFactory.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin;

/** Creates a fresh plugin instance for one task. */
@FunctionalInterface
public interface PluginFactory {
    OpenDataPlugin create(ResolvedPlugin plugin);
}
