/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

/**
 * Point-in-time connection-pool utilisation values.
 * @param activeConnections number of active pooled connections
 * @param idleConnections number of idle pooled connections
 * @param maximumConnections maximum configured pooled connections
 * @param closed whether the pool has been closed
  *
 * @author Terry Curran
 * @version 17 July 2026
 */
public record DatabasePoolSnapshot(
        int activeConnections,
        int idleConnections,
        int maximumConnections,
        boolean closed) {
}
