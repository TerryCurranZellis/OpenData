/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

/**
 * Ensures that the shared dry-run resource cannot borrow a connection.
 *
 * @author Terry Curran
 * @since 4.2.0
 * @version 4.2.0
 */
class UnavailableDatabaseResourceManagerTest {

    @Test
    void rejectsDatabaseAccess() {
        var database = new UnavailableDatabaseResourceManager();
        assertThrows(DatabaseException.class, database::getConnection);
        database.close();
    }
}
