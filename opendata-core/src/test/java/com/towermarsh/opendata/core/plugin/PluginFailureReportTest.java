package com.towermarsh.opendata.core.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class PluginFailureReportTest {

    @Test
    void includesTypeCauseChainAndSqlDetails() {
        var failure = new IllegalStateException("outer", new SQLException("Invalid object name", "42S02", 208));
        var text = PluginFailureReport.describe(failure);
        assertTrue(text.contains("Error: java.lang.IllegalStateException: outer"));
        assertTrue(text.contains("Caused by: java.sql.SQLException: Invalid object name [sqlState=42S02, errorCode=208]"));
    }

    @Test
    void isTruncatedToAuditColumnLength() {
        var text = PluginFailureReport.describe(new RuntimeException("x".repeat(10_000)));
        assertEquals(PluginFailureReport.MAX_LENGTH, text.length());
    }

    @Test
    void identifiesPipelineStep() {
        var failure = new RuntimeException("boom");
        failure.setStackTrace(new StackTraceElement[]{new StackTraceElement(
                "com.towermarsh.opendata.plugin.octopus.load.OctopusLoad", "load", "OctopusLoad.java", 10)});
        assertTrue(PluginFailureReport.describe(failure).startsWith("Step: load"));
    }
}
