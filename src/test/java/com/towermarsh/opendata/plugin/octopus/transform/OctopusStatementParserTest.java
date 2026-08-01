/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/**
 * Tests typed Octopus statement parsing helpers.
 *
 * @author Terry Curran
 * @version 2.0.0
 */
class OctopusStatementParserTest {

    @Test
    void buildsTypedElectricityRecordFromParsedSection() {
        final var record = OctopusStatementParser.newElectricityRecord(
                """
                Electricity Supply number 12345 2000012845052
                Energy Charges for Meter 20E5013326
                About Your Tariff Octopus Flexible Tariff (7th Dec. 2021 - 5th Jan. 2022)
                Unit Rate 15.51p/kWh
                7th Dec. 2021 2257.0 Smart meter reading
                5th Jan. 2022 2481.3 Actual reading
                Energy Used 224.3 kWh @ 15.51p/kWh
                Standing Charge 31 days @ 19.23p/day £5.96
                Total Electricity Charges £42.78
                """,
                "2022-01-05",
                "2021-12-07",
                "2022-01-05");

        assertEquals(LocalDate.of(2022, 1, 5), record.billDate());
        assertEquals("Octopus Flexible Tariff", record.tariffName());
        assertEquals(new BigDecimal("2257.0"), record.startReadingValue());
        assertEquals(new BigDecimal("42.78"), record.totalCostGbp());
    }

    @Test
    void rejectsElectricitySectionWhenRequiredDecimalIsMissing() {
        assertThrows(IllegalArgumentException.class, () -> OctopusStatementParser.newElectricityRecord(
                """
                Electricity Supply number 12345 2000012845052
                Energy Charges for Meter 20E5013326
                Octopus Flexible Tariff (7th Dec. 2021 - 5th Jan. 2022)
                7th Dec. 2021 2257.0 Smart meter reading
                5th Jan. 2022 2481.3 Actual reading
                Energy Used 224.3 kWh @ 15.51p/kWh
                Standing Charge 31 days @ 19.23p/day £5.96
                Total Electricity Charges
                """,
                "2022-01-05",
                "2021-12-07",
                "2022-01-05"));
    }
}
