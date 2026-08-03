/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Plugin discovery, execution, auditing, concurrency, and common exception
 * handling. Concrete plugins use initialise, extract, transform, load, and
 * finalise phase packages. {@link com.towermarsh.opendata.plugin.PluginExceptionHandler}
 * converts phase failures into the common framework plugin exception.
 */
package com.towermarsh.opendata.plugin;
