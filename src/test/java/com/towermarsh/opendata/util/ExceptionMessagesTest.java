/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests safe exception message extraction. @version 2.1 */
class ExceptionMessagesTest {

    @Test
    void returnsDeepestCauseMessage() {
        final var exception = new IllegalStateException("outer", new IllegalArgumentException("root"));
        assertEquals("root", ExceptionMessages.rootCauseMessage(exception));
    }

    @Test
    void usesClassNameWhenMessageIsBlank() {
        assertEquals("IllegalArgumentException",
                ExceptionMessages.rootCauseMessage(new IllegalArgumentException(" ")));
    }

    @Test
    void terminatesForCircularCauseChains() {
        final var first = new CircularThrowable("first");
        final var second = new CircularThrowable("second");
        first.cause = second;
        second.cause = first;
        assertEquals("second", ExceptionMessages.rootCauseMessage(first));
    }

    private static final class CircularThrowable extends RuntimeException {
        private static final long serialVersionUID = 1L;
        private Throwable cause;

        CircularThrowable(final String message) {
            super(message, null);
        }

        @Override
        public synchronized Throwable getCause() {
            return cause;
        }
    }
}
