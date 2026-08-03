package com.towermarsh.opendata.plugin.octopus.extract;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.nio.file.Path;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class OctopusExtractTest {
    @Test void parsesStatementDateFromExpectedFilename() {
        assertEquals(LocalDate.of(2026, 7, 31),
                OctopusExtract.statementDate(Path.of("octopus-energy-statement-2026-07-31.pdf")));
    }
    @Test void rejectsOtherPdfNames() {
        assertThrows(IllegalArgumentException.class,
                () -> OctopusExtract.statementDate(Path.of("statement-2026-07-31.pdf")));
    }
}
