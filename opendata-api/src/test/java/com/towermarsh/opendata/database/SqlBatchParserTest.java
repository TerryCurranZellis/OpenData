/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Verifies SQL Server GO-delimited setup script batch extraction.
 *
 * @author Terry Curran
 * @since 4.1.0
 * @version 4.1.0
 */
class SqlBatchParserTest {

    /**
     * Separators are accepted with mixed case and surrounding whitespace.
     */
    @Test
    void splitsAtStandaloneCaseInsensitiveGoSeparators() {
        var script = "CREATE TABLE dbo.A (Id int);\n Go \n"
                + "INSERT INTO dbo.A VALUES (1);\nGO\n";
        assertEquals(List.of("CREATE TABLE dbo.A (Id int);",
                "INSERT INTO dbo.A VALUES (1);"), SqlBatchParser.batches(script));
    }

    /**
     * Empty and comment-only sections do not become executable statements.
     */
    @Test
    void skipsBlankAndCommentOnlyBatches() {
        var script = "-- initial remark\nGO\n/* no executable statements */\nGO\n"
                + "SELECT 1;\nGO\n-- trailing note\n";
        assertEquals(List.of("SELECT 1;"), SqlBatchParser.batches(script));
    }

    /**
     * The returned list cannot be modified by setup consumers.
     */
    @Test
    void returnsImmutableBatches() {
        var batches = SqlBatchParser.batches("SELECT 1;");
        assertThrows(UnsupportedOperationException.class, () -> batches.add("SELECT 2;"));
    }

    /**
     * A missing script is rejected before processing.
     */
    @Test
    void rejectsNullScript() {
        assertThrows(NullPointerException.class, () -> SqlBatchParser.batches(null));
    }
}
