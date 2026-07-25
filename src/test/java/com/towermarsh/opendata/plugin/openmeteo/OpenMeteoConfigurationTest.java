package com.towermarsh.opendata.plugin.openmeteo;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.time.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class OpenMeteoConfigurationTest {
    private OpenMeteoConfiguration config(Optional<LocalDate> start, Optional<LocalDate> end, boolean current) {
        return new OpenMeteoConfiguration(URI.create("https://archive-api.open-meteo.com/v1/archive"), "home", "Home",
                51.6207,-1.1098, ZoneId.of("Europe/London"), Duration.ofSeconds(10), Duration.ofSeconds(30),
                start,end,365,current,"openmeteo","Location","DailyWeather",500,Duration.ofSeconds(30));
    }
    @Test void resolvesConfiguredRange() {
        var c=config(Optional.of(LocalDate.of(2020,1,1)),Optional.of(LocalDate.of(2020,1,31)),false);
        assertEquals(LocalDate.of(2020,1,1),c.resolveDateRange(LocalDate.now()).startDate());
    }
    @Test void excludesCurrentDateByDefault() {
        var r=config(Optional.empty(),Optional.empty(),false).resolveDateRange(LocalDate.of(2026,7,25));
        assertEquals(LocalDate.of(2026,7,24),r.endDate());
        assertEquals(LocalDate.of(2025,7,24),r.startDate());
    }
    @Test void includesCurrentDateWhenRequested() { assertEquals(LocalDate.of(2026,7,25),config(Optional.empty(),Optional.empty(),true).resolveDateRange(LocalDate.of(2026,7,25)).endDate()); }
    @Test void rejectsUnsafeSqlIdentifier() { assertThrows(IllegalArgumentException.class, () -> new OpenMeteoConfiguration(URI.create("https://x"),"x","X",0,0,ZoneId.of("UTC"),Duration.ofSeconds(1),Duration.ofSeconds(1),Optional.empty(),Optional.empty(),1,false,"dbo;drop","L","D",1,Duration.ofSeconds(1))); }
    @Test void rejectsInvertedDates() { assertThrows(IllegalArgumentException.class, () -> config(Optional.of(LocalDate.of(2026,2,1)),Optional.of(LocalDate.of(2026,1,1)),false)); }
    @Test void rejectsInvalidBatchSize() { assertThrows(IllegalArgumentException.class, () -> new OpenMeteoConfiguration(URI.create("https://x"),"x","X",0,0,ZoneId.of("UTC"),Duration.ofSeconds(1),Duration.ofSeconds(1),Optional.empty(),Optional.empty(),1,false,"dbo","L","D",0,Duration.ofSeconds(1))); }
}
