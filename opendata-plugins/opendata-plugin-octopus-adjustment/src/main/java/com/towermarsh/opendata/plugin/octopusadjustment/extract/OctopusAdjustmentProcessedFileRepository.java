/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.extract;

import com.towermarsh.opendata.common.database.jdbc.JdbcCompletedFileKeys;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import java.util.Objects;
import java.util.Set;

/**
 * Reads completed Octopus adjustment source identities.
 *
 * @author Terry Curran
 * @version 4.2.0
 * @since 3.1.0
 */
public final class OctopusAdjustmentProcessedFileRepository {

    private final DatabaseResourceManager database;

    /**
     * Creates the repository.
     *
     * @param database database resource manager
     * @since 3.1.0
     */
    public OctopusAdjustmentProcessedFileRepository(DatabaseResourceManager database) {
        this.database = Objects.requireNonNull(database, "database");
    }

    /**
     * Returns completed source keys in
     * {@code lower-case-file-name|lower-case-hash} form.
     *
     * @return immutable completed-key set
     * @since 3.1.0
     */
    public Set<String> findProcessedFileKeys() {
        return JdbcCompletedFileKeys.read(
                database, "octopus", "adjustment_file", "Unable to read processed Octopus adjustment files. Re-register the octopus-adjustment plugin to create its tables.");
    }

    /**
     * Builds the normalised completion key.
     *
     * @param fileName source filename
     * @param sha256 source hash
     * @return normalised key
     * @since 3.1.0
     */
    public static String key(String fileName, String sha256) {
        return JdbcCompletedFileKeys.key(fileName, sha256);
    }
}
