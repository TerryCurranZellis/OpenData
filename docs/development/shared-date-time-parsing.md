# Shared date/time parsing utilities

The `opendata-common` Maven module provides `com.towermarsh.opendata.util.DateTimeParsers` for reusable parsing and formatting. `opendata-api` stays focused on plugin interfaces and records; plugins must not depend on `opendata-core`.

## Supported local dates

The `parseDate(String)` method tries cached, strict, thread-safe `DateTimeFormatter` instances in order:

| Input example | Interpretation | Pattern |
| --- | --- | --- |
| `2023-01-01` | 1 January 2023 | ISO `uuuu-MM-dd` |
| `01/01/2023` | 1 January 2023 | `dd/MM/uuuu` |
| `01-JAN-2009` | 1 January 2009 | `dd-MMM-uuuu` |
| `01-Jan-23` | 1 January 2023 | `dd-MMM-uu` |

Abbreviated month names are **case-insensitive and English-locale-specific**. A two-digit year is interpreted as **2000–2099**. Invalid dates (such as 31 February) are rejected. US `MM/dd/uuuu` is **not** automatically guessed, because numeric dates are ambiguous.

## API examples

```java
import com.towermarsh.opendata.util.DateTimeParsers;
import java.time.LocalDate;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

LocalDate date = DateTimeParsers.parseDate("01-Jan-23");
String isoDate = DateTimeParsers.formatDate(date); // 2023-01-01
Instant utc = DateTimeParsers.parseInstant("2025-01-01 00:30:00");

// A provider-specific format is opt-in; no global ambiguity is introduced.
DateTimeFormatter us = DateTimeFormatter.ofPattern("MM/dd/uuuu", Locale.US);
LocalDate special = DateTimeParsers.parseDate("12/31/2023", List.of(us));
```

`parseInstant` accepts an ISO instant, an ISO offset timestamp, or a local ISO timestamp (space or `T` separator). **Local timestamps with no offset are interpreted as UTC**, matching existing NESO behaviour. Use explicit offset timestamps for sources in local time zones.

`NesoCsv.parseDate` and `NesoCsv.parseInstant` remain as compatibility wrappers and now call `DateTimeParsers`; the Electricity Demand/Supply transform code is unchanged. Other plugins can migrate incrementally once their specific date conventions are verified.

## Maven dependency and rebuild

The electricity support module adds the already-managed `opendata-common` dependency. Any new provider using the utilities should also declare `opendata-common` as a Maven dependency.

From the OpenData repository root:

```powershell
mvn -pl opendata-core -am clean install
```

If using a packaged application, replace/rebuild all runtime JARs together so `opendata-common` and `opendata-plugin-electricity-common` are from the same build. Run the unit tests and a demand-plugin dry run after deploying.
