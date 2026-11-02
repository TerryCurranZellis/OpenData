/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.core.logging;

import com.towermarsh.opendata.common.logging.PluginLogContext;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Thread-safe JUL formatter containing thread, plugin, and run context.
 *
 *
 * @author Terry Curran
 * @version 1.0.0
 */
public class ContextualLogFormatter extends Formatter {

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            .withZone(ZoneId.systemDefault());

    /**
     *
     * @param record
     * @return
     */
    @Override
    public String format(LogRecord record) {

        var builder = new StringBuilder()
                .append("[").append(TIMESTAMP.format(Instant.ofEpochMilli(record.getMillis())))
                .append("]:[").append(record.getLevel().getName())
                .append("]:[").append(Thread.currentThread().getName()).append(']');

        PluginLogContext.current().ifPresent((var context) -> builder
                .append("[").append(context.pluginId()).append(']')
                .append(":[").append(context.runId()).append(']'));

        var loggerName = record.getLoggerName();
        var lastDot = loggerName.lastIndexOf('.');
        var shortLoggerName = lastDot >= 0 ? loggerName.substring(lastDot + 1) : loggerName;

        builder.append(shortLoggerName).append(" - ")
                .append(formatMessage(record)).append(System.lineSeparator());

        if (record.getThrown() != null) {
            var text = new StringWriter();
            record.getThrown().printStackTrace(new PrintWriter(text));
            builder.append(text);
        }

        return builder.toString();
    }
}
