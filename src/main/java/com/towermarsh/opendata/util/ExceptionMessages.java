/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.util;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Set;

/**
 * Extracts stable, user-facing messages from exception cause chains.
 *
 * @author Terry Curran
 * @version 2.1
 */
public final class ExceptionMessages {

    private ExceptionMessages() {
        // Utility class.
    }

    /**
     * Returns the deepest unique cause message, or its simple class name when
     * no message is available. Circular cause chains are handled safely.
     *
     * @param exception exception to inspect
     * @return user-facing root-cause message
     */
    public static String rootCauseMessage(final Throwable exception) {
        var current = Objects.requireNonNull(exception, "exception");
        final Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        visited.add(current);
        while (current.getCause() != null && visited.add(current.getCause())) {
            current = current.getCause();
        }
        final var message = current.getMessage();
        return message == null || message.isBlank()
                ? current.getClass().getSimpleName()
                : message;
    }
}
