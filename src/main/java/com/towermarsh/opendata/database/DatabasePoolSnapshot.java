/*
 * (c) Copyright 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

/**
 * Point-in-time connection-pool utilisation values.
 */
public record DatabasePoolSnapshot(
        int activeConnections,
        int idleConnections,
        int maximumConnections,
        boolean closed) {
}
