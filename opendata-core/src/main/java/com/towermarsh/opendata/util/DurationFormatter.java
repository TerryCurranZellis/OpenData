/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.util;

import java.time.Duration;
import java.util.Objects;

/**
 * Formats elapsed application and plugin durations consistently.
 *
 * @author Terry Curran
 * @version 2.1
 */
public final class DurationFormatter {

    private DurationFormatter() {
        // Utility class.
    }

    /**
     * Formats a non-negative duration as total-hours {@code HH:mm:ss}.
     *
     * @param duration elapsed duration
     * @return formatted duration
     * @throws IllegalArgumentException when the duration is negative
     */
    public static String formatElapsed(final Duration duration) {
        final var value = Objects.requireNonNull(duration, "duration");
        if (value.isNegative()) {
            throw new IllegalArgumentException("duration must not be negative");
        }
        return String.format(
                "%02d:%02d:%02d",
                value.toHours(),
                value.toMinutesPart(),
                value.toSecondsPart());
    }
}
