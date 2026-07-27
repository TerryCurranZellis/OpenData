/*
 * Filename: OfgemWorkbookDataValidatorTest.java
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
package com.towermarsh.opendata.plugin.ofgem.transform.validate;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.towermarsh.opendata.exception.ImportException;
import com.towermarsh.opendata.plugin.ofgem.transform.model.OfgemPriceCapLevel;
import com.towermarsh.opendata.plugin.ofgem.transform.model.OfgemPriceCapPeriod;
import com.towermarsh.opendata.plugin.ofgem.transform.model.OfgemPriceCapWorkbookData;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * @author Terry Curran
 * @version 17 July 2026
 */
class OfgemWorkbookDataValidatorTest {
    private final OfgemWorkbookDataValidator validator = new OfgemWorkbookDataValidator();

    @Test
    void returnsValidWorkbookData() throws ImportException {
        final var data = data(level("A1", "NORTH_WEST"));
        assertSame(data, validator.validate(data));
    }

    @Test
    void rejectsDuplicateBusinessKeys() {
        final var data = data(
                level("A1", "NORTH_WEST"),
                level("A2", "NORTH_WEST"));
        assertThrows(ImportException.class, () -> validator.validate(data));
    }

    private static OfgemPriceCapWorkbookData data(final OfgemPriceCapLevel... levels) {
        return new OfgemPriceCapWorkbookData(
                new OfgemPriceCapPeriod(
                        "January 2026 - March 2026",
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2026, 3, 31),
                        1,
                        true),
                List.of(levels));
    }

    private static OfgemPriceCapLevel level(final String cell, final String region) {
        return new OfgemPriceCapLevel(
                region,
                "OTHER",
                "GAS",
                "NIL",
                BigDecimal.TEN,
                false,
                "1a Levelised DTC",
                cell);
    }
}
