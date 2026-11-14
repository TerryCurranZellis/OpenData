package com.towermarsh.opendata.common.database;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import com.towermarsh.opendata.database.SqlBatchParser;

/**
 * Regression tests for SqlScriptExecutor behaviour.
 *
 * @author Terry Curran
 * @version 4.2.0
 */
class SqlScriptExecutorTest {

    @Test
    void splitsOnGoAndDropsCommentOnlyBatches() {
        var batches = SqlBatchParser.batches("/* header */\nGO\nCREATE TABLE a (x int);\nGO\n-- note\nGO\nSELECT 1\nGO\n");
        assertEquals(2, batches.size());
        assertEquals("CREATE TABLE a (x int);", batches.get(0));
    }
}
