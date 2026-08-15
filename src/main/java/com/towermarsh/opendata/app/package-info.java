/*
 * Copyright Â© 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Application bootstrap and aggregate execution status.
 *
 * <ul>
 * <li>{@link ApplicationInfo} - application identity and ru-ntime metadata</li>
 * <li>{@link ExecutionStatus} - Final status reported by one OpenData process invocation.
 * <p>
 * StatusCode is not yet used as it will be a return stats for the
 * application to be interpreted by the OS.</p></li>
 * <li>{@link OpenDataApplication} - Coordinates plugin administration, registry selection, 
 * configuration, pooled database access, and plugin execution.</li>
 * </ul>
  *
 * @author Terry Curran
 * @version 1.0.0
 */
package com.towermarsh.opendata.app;
