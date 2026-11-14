/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

import java.sql.Connection;

/**
 * Fail-closed database resource used by plugins during non-writing dry runs.
 *
 * <p>This implementation is shared by Core and plugin tests without introducing
 * any dependency on the Core application from provider plugins.</p>
 *
 * @author Terry Curran
 * @since 4.2.0
 * @version 4.2.0
 */
public class UnavailableDatabaseResourceManager implements DatabaseResourceManager {

    /**
     * Always rejects database access to prevent writes during dry runs.
     *
     * @return never returns normally
     * @throws DatabaseException always, because connections are disabled
     */
    @Override
    public Connection getConnection() throws DatabaseException {
        throw new DatabaseException("Database access is disabled during --dry-run.");
    }

    /**
     * Releases no resources because this marker never opens connections.
     */
    @Override
    public void close() {
        // No resources to release.
    }
}
