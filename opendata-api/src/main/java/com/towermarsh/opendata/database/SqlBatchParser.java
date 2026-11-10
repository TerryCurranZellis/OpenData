/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Splits SQL Server setup scripts at standalone GO batch separators.
 *
 * <p>The scanner tracks SQL quoted text, bracketed identifiers, and comments
 * so that GO inside a string or block comment is not treated as a separator.
 * Only standalone GO lines are recognised (not GO repetition counts).</p>
 *
 * @author Terry Curran
 * @since 4.1.0
 * @version 4.1.0
 */
public final class SqlBatchParser {

    private SqlBatchParser() {
    }

    /**
     * Returns executable non-empty batches in script order.
     *
     * @param script SQL Server script text
     * @return immutable list of executable batches
     */
    public static List<String> batches(String script) {
        Objects.requireNonNull(script, "script");
        var result = new ArrayList<String>();
        var batch = new StringBuilder();
        var state = new ScanState();

        int offset = 0;
        while (offset < script.length()) {
            int end = offset;
            while (end < script.length()
                    && script.charAt(end) != '\n'
                    && script.charAt(end) != '\r') {
                end++;
            }

            int next = end;
            if (next < script.length()) {
                char newline = script.charAt(next++);
                if (newline == '\r' && next < script.length()
                        && script.charAt(next) == '\n') {
                    next++;
                }
            }

            String line = script.substring(offset, end);
            if (state.mode == Mode.NORMAL && line.strip().equalsIgnoreCase("GO")) {
                addBatch(result, batch, state.executable);
                batch.setLength(0);
                state.executable = false;
            } else {
                batch.append(script, offset, next);
                scan(line, state);
            }
            offset = next;
        }

        addBatch(result, batch, state.executable);
        return List.copyOf(result);
    }

    private static void addBatch(List<String> result, StringBuilder batch, boolean executable) {
        if (executable) {
            result.add(batch.toString().strip());
        }
    }

    private static void scan(String line, ScanState state) {
        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            char next = i + 1 < line.length() ? line.charAt(i + 1) : '\0';
            switch (state.mode) {
                case NORMAL -> {
                    if (current == '-' && next == '-') {
                        return;
                    }
                    if (current == '/' && next == '*') {
                        state.mode = Mode.BLOCK_COMMENT;
                        i++;
                    } else if (current == '\'') {
                        state.mode = Mode.SINGLE_QUOTE;
                        state.executable = true;
                    } else if (current == '"') {
                        state.mode = Mode.DOUBLE_QUOTE;
                        state.executable = true;
                    } else if (current == '[') {
                        state.mode = Mode.BRACKET_IDENTIFIER;
                        state.executable = true;
                    } else if (!Character.isWhitespace(current)) {
                        state.executable = true;
                    }
                }
                case BLOCK_COMMENT -> {
                    if (current == '*' && next == '/') {
                        state.mode = Mode.NORMAL;
                        i++;
                    }
                }
                case SINGLE_QUOTE -> {
                    if (current == '\'') {
                        if (next == '\'') {
                            i++;
                        } else {
                            state.mode = Mode.NORMAL;
                        }
                    }
                }
                case DOUBLE_QUOTE -> {
                    if (current == '"') {
                        if (next == '"') {
                            i++;
                        } else {
                            state.mode = Mode.NORMAL;
                        }
                    }
                }
                case BRACKET_IDENTIFIER -> {
                    if (current == ']') {
                        if (next == ']') {
                            i++;
                        } else {
                            state.mode = Mode.NORMAL;
                        }
                    }
                }
            }
        }
    }

    private enum Mode {
        NORMAL,
        SINGLE_QUOTE,
        DOUBLE_QUOTE,
        BRACKET_IDENTIFIER,
        BLOCK_COMMENT
    }

    private static final class ScanState {

        private Mode mode = Mode.NORMAL;
        private boolean executable;
    }
}
