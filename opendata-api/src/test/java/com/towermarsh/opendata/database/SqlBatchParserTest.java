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

    /**
     * A GO line inside a multiline SQL literal is not a separator.
     */
    @Test
    void doesNotSplitMultilineSqlString() {
        var script = "SELECT 'first line\nGO\nlast line' AS Value;\nGO\nSELECT 2;";
        assertEquals(List.of("SELECT 'first line\nGO\nlast line' AS Value;",
                "SELECT 2;"), SqlBatchParser.batches(script));
    }

    /**
     * GO in a block comment is ignored, even across line boundaries.
     */
    @Test
    void ignoresGoInsideBlockComment() {
        var script = "/* note\nGO\n*/\nSELECT 1;\nGO\nSELECT 2;";
        assertEquals(List.of("/* note\nGO\n*/\nSELECT 1;",
                "SELECT 2;"), SqlBatchParser.batches(script));
    }

    /**
     * SQL strings containing comment syntax must remain executable.
     */
    @Test
    void preservesCommentMarkersInsideQuotedStrings() {
        var script = "SELECT '--not a comment', '/* not a comment */';\nGO\n";
        assertEquals(List.of("SELECT '--not a comment', '/* not a comment */';"),
                SqlBatchParser.batches(script));
    }

    /**
     * Quoted multiline identifiers and escaped quote characters are respected.
     */
    @Test
    void preservesEscapedQuotesAndQuotedIdentifiers() {
        var script = "SELECT 'it''s fine', [column\nGO\nname] FROM dbo.Example;\nGO\n";
        assertEquals(List.of("SELECT 'it''s fine', [column\nGO\nname] FROM dbo.Example;"),
                SqlBatchParser.batches(script));
    }

    /**
     * SQL setup scripts can contain Windows CRLF line endings.
     */
    @Test
    void supportsWindowsLineEndings() {
        var script = "SELECT 1;\r\nGO\r\nSELECT 2;\r\n";
        assertEquals(List.of("SELECT 1;", "SELECT 2;"), SqlBatchParser.batches(script));
    }

    /**
     * Inline GO text is not a batch separator.
     */
    @Test
    void doesNotSplitGoInsideSqlStatement() {
        assertEquals(List.of("SELECT GO FROM dbo.Example;"),
                SqlBatchParser.batches("SELECT GO FROM dbo.Example;"));
    }

}
