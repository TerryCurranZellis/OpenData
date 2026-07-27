/*
 * Filename: OpenMeteoResponseValidatorTest.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The author may be contacted by email to the following address:
 *
 * terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.plugin.openmeteo.transform.validate;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.towermarsh.opendata.plugin.openmeteo.extract.OpenMeteoResponse;
import com.towermarsh.opendata.plugin.openmeteo.exception.OpenMeteoException;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * @author Terry Curran
 * @version 17 July 2026
 */
class OpenMeteoResponseValidatorTest {
    private final OpenMeteoResponseValidator validator = new OpenMeteoResponseValidator();

    @Test
    void returnsResponseWhenDailyArraysHaveMatchingLengths() throws OpenMeteoException {
        final var response = response(List.of(1));
        assertSame(response, validator.validate(response));
    }

    @Test
    void rejectsMismatchedDailyArrayLengths() {
        final var response = response(List.of());
        assertThrows(OpenMeteoException.class, () -> validator.validate(response));
    }

    private static OpenMeteoResponse response(final List<Integer> weatherCodes) {
        return new OpenMeteoResponse(
                51.5,
                -0.1,
                "Europe/London",
                new OpenMeteoResponse.Daily(
                        List.of("2026-01-01"),
                        List.of(5.0),
                        List.of(1.0),
                        List.of(3.0),
                        List.of("2026-01-01T08:00"),
                        List.of("2026-01-01T16:00"),
                        List.of(28_800.0),
                        weatherCodes));
    }
}
