/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import org.junit.jupiter.api.Test;

/** Tests shared elapsed-duration formatting. @version 2.1 */
class DurationFormatterTest {

    @Test
    void formatsZeroAndSubHourDurations() {
        assertEquals("00:00:00", DurationFormatter.formatElapsed(Duration.ZERO));
        assertEquals("00:02:03", DurationFormatter.formatElapsed(Duration.ofSeconds(123)));
    }

    @Test
    void preservesTotalHoursBeyondOneDay() {
        assertEquals("49:02:03", DurationFormatter.formatElapsed(Duration.ofHours(49).plusMinutes(2).plusSeconds(3)));
    }

    @Test
    void rejectsNegativeDurations() {
        assertThrows(IllegalArgumentException.class,
                () -> DurationFormatter.formatElapsed(Duration.ofSeconds(-1)));
    }
}
