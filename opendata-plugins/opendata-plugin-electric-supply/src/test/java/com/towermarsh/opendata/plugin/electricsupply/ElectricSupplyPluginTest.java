package com.towermarsh.opendata.plugin.electricsupply;
 import org.junit.jupiter.api.Test;
 import java.time.LocalDate;
 import java.time.Instant;
 import static org.junit.jupiter.api.Assertions.*;
 class ElectricSupplyPluginTest {
 @Test void dateFormats() {assertEquals(LocalDate.of(2025,1,1),ElectricSupplyPlugin.parseDate("01/01/2025"));assertEquals(LocalDate.of(2025,1,1),ElectricSupplyPlugin.parseDate("2025-01-01"));}
 @Test void timestampUtc() {assertEquals(Instant.parse("2025-01-01T00:30:00Z"),ElectricSupplyPlugin.parseInstant("2025-01-01T00:30:00+00:00"));}
 @Test void yearSelection() {assertTrue(ElectricSupplyPlugin.includesYear("Historic Demand Data 2025",LocalDate.of(2025,1,1),LocalDate.of(2025,12,31)));}
 }