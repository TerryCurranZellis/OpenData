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
