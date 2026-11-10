/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests safe exception message extraction. @version 4.1.0
 * @author Terry Curran
 */
class ExceptionMessagesTest {

    @Test
    void returnsDeepestCauseMessage() {
        var exception = new IllegalStateException("outer", new IllegalArgumentException("root"));
        assertEquals("root", ExceptionMessages.rootCauseMessage(exception));
    }

    @Test
    void usesClassNameWhenMessageIsBlank() {
        assertEquals("IllegalArgumentException",
                ExceptionMessages.rootCauseMessage(new IllegalArgumentException(" ")));
    }

    @Test
    void terminatesForCircularCauseChains() {
        var first = new CircularThrowable("first");
        var second = new CircularThrowable("second");
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
