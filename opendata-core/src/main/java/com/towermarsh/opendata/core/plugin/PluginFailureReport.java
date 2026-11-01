/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.core.plugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Builds the detailed failure text stored in {@code core.PluginRun.ErrorMessage}
 * and written to the log when a plugin fails.
 *
 * <p>The report names the pipeline step that failed (initialise, extract,
 * transform, load or finalise) when it can be determined from the stack trace,
 * the full cause chain with exception types, SQL state and error codes, and the
 * first plugin stack frames of the root cause.</p>
 *
 * @author Terry Curran
 * @version 3.1.0
 */
public final class PluginFailureReport {

    /** Maximum length written to the audit table. */
    public static final int MAX_LENGTH = 4000;

    private static final Set<String> STEPS
            = Set.of("initialise", "extract", "transform", "load", "finalise");
    private static final String PLUGIN_PACKAGE = "com.towermarsh.opendata.plugin.";
    private static final int MAX_FRAMES = 6;
    private static final int MAX_CAUSES = 8;

    private PluginFailureReport() {
    }

    /**
     * Describes a plugin failure.
     *
     * @param failure the failure
     * @return multi-line description, at most {@link #MAX_LENGTH} characters
     */
    public static String describe(final Throwable failure) {
        Objects.requireNonNull(failure, "failure");
        final var text = new StringBuilder();
        step(failure).ifPresent(step -> text.append("Step: ").append(step).append('\n'));

        final List<Throwable> chain = new ArrayList<>();
        for (Throwable t = failure; t != null && chain.size() < MAX_CAUSES && !chain.contains(t); t = t.getCause()) {
            chain.add(t);
        }
        for (var i = 0; i < chain.size(); i++) {
            text.append(i == 0 ? "Error: " : "Caused by: ").append(summary(chain.get(i))).append('\n');
        }
        final var root = chain.get(chain.size() - 1);
        var frames = 0;
        for (var frame : root.getStackTrace()) {
            if (frame.getClassName().startsWith("com.towermarsh.opendata")) {
                text.append("  at ").append(frame).append('\n');
                if (++frames >= MAX_FRAMES) {
                    break;
                }
            }
        }
        final var result = text.toString().stripTrailing();
        return result.length() <= MAX_LENGTH ? result : result.substring(0, MAX_LENGTH);
    }

    static java.util.Optional<String> step(final Throwable failure) {
        final var chain = new ArrayList<Throwable>();
        for (Throwable t = failure; t != null && chain.size() < MAX_CAUSES && !chain.contains(t); t = t.getCause()) {
            chain.add(t);
        }
        for (var t : chain) {
            for (var frame : t.getStackTrace()) {
                final var name = frame.getClassName();
                if (!name.startsWith(PLUGIN_PACKAGE)) {
                    continue;
                }
                final var parts = name.substring(PLUGIN_PACKAGE.length()).split("\\.");
                if (parts.length >= 3 && STEPS.contains(parts[1].toLowerCase(Locale.ROOT))) {
                    return java.util.Optional.of(parts[1].toLowerCase(Locale.ROOT)
                            + " (" + frame.getClassName() + "." + frame.getMethodName() + ")");
                }
            }
        }
        return java.util.Optional.empty();
    }

    private static String summary(final Throwable t) {
        final var message = t.getMessage();
        final var text = new StringBuilder(t.getClass().getName());
        if (message != null && !message.isBlank()) {
            text.append(": ").append(message.strip());
        }
        if (t instanceof SQLException sql) {
            text.append(" [sqlState=").append(sql.getSQLState())
                    .append(", errorCode=").append(sql.getErrorCode()).append(']');
        }
        return text.toString();
    }
}
