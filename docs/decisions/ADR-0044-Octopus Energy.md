# ADR-043: Octopus Energy

- Status: Proposed
- Date: 2026-07-27
- Decision owners: OpenData maintainers

## Context

Octopus enery statement arrives as a PDF attachment to email

## Decision

Download email attachment and convert to database records

## Consequences

### Positive

- Electricity statement
- Gas statement

### Negative or limiting

- n/a

## Alternatives considered

### Alternative

Manual Process

## Implementation notes

- From: Octopus Energy <hello@octopus.energy>
- Subject: FYI: Your energy statement
- Attachment: octopus-energy-statement-<ISO_DATE>.pdf
- Limit to 12 months
- Email Server
- Email credentials

## Java Code

Some code already exists as I currntly d a manual download

### OctopusStatementParser

#### Constructor
```Java
    /**
     * Construct a parser that will search {@code inputDirectory} for Octopus
     * Energy PDF files.
     *
     * @param inputDirectory directory containing
     * {@code octopus-energy-statement-YYYY-MM-DD.pdf} files; must exist
     */
    public OctopusStatementParser(Path inputDirectory) {
        this.inputDirectory = inputDirectory;
    }
```
#### parseElectricityFromFile
```java
 /**
     * Parse electricity records from a single PDF file that does not follow
     * the {@code octopus-energy-statement-YYYY-MM-DD.pdf} naming convention.
     *
     * @param pdfPath path to the PDF file to parse
     * @return list of extracted electricity records; never {@code null}
     * @throws IOException if the PDF cannot be read
     */
    public static List<ElectricityRecord> parseElectricityFromFile(Path pdfPath)
            throws IOException {
```

#### parseGasFromFile
```Java
    /**
     * Parse gas records from a single PDF file that does not follow the
     * {@code octopus-energy-statement-YYYY-MM-DD.pdf} naming convention.
     *
     * @param pdfPath path to the PDF file to parse
     * @return list of extracted gas records; never {@code null}; will be empty
     *         for files that contain no gas section (e.g. electricity-only
     *         adjustment bills)
     * @throws IOException if the PDF cannot be read
     */
    public static List<GasRecord> parseGasFromFile(Path pdfPath)
            throws IOException {
```

#### parseElectricity
```Java
    /**
     * Parse all PDF files in the input directory and return electricity
     * records.
     *
     * @return list of extracted electricity records; never {@code null}
     * @throws IOException if any PDF file cannot be read or its text cannot be
     * extracted
     */
    public List<ElectricityRecord> parseElectricity() throws IOException 
```
#### parseGas
```Java
    /**
     * Parse all PDF files in the input directory and return gas records.
     *
     * @return list of extracted gas records; never {@code null}
     * @throws IOException if any PDF file cannot be read or its text cannot be
     * extracted
     */
    public List<GasRecord> parseGas() throws IOException
```

### PDFTextExtractor

#### extract
```Java
    /**
     * Extract all text from the PDF at the given path.
     *
     * <p>
     * The returned string preserves the original line endings from the PDF text
     * stream. Encrypted PDFs that require a password will throw an
     * {@link IOException}.
     *
     * @param pdfPath path to the PDF file to extract text from
     * @return the full text content of the PDF as returned by
     * {@link PDFTextStripper}; never {@code null}
     * @throws IOException if the file cannot be read, is not a valid PDF, or is
     * encrypted and cannot be opened without a password
     */
    public static String extract(Path pdfPath) throws IOException {
        try (var document = Loader.loadPDF(pdfPath.toFile())) {
            var stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
```
### ElectricityRecord
- will need validating
	- all data is string
	- unit rate/standing charge is in pence (100pence=1pound)
	- readings are decimal number
	- totals are in pounds (GBP)
```Java
/**
 * Immutable record holding one row of extracted electricity billing data,
 *
 * @param billDate                statement date in {@code yyyy-MM-dd} format
 * @param billPeriodStart         overall bill period start in {@code yyyy-MM-dd}
 * @param billPeriodEnd           overall bill period end in {@code yyyy-MM-dd}
 * @param tariffName              Octopus tariff name
 * @param tariffPeriodStart       tariff sub-period start in {@code yyyy-MM-dd}
 * @param tariffPeriodEnd         tariff sub-period end in {@code yyyy-MM-dd}
 * @param mpan                    13-digit Meter Point Administration Number
 * @param meterId                 physical meter serial number
 * @param startReadingDate        opening meter reading date in {@code yyyy-MM-dd}
 * @param startReadingValue       opening meter reading (kWh) as a string
 * @param startReadingType        how the opening reading was obtained
 * @param endReadingDate          closing meter reading date in {@code yyyy-MM-dd}
 * @param endReadingValue         closing meter reading (kWh) as a string
 * @param endReadingType          how the closing reading was obtained
 * @param energyUsedKwh           energy consumed (kWh) as a string; may be negative
 * @param unitRatePKwh            unit rate in pence per kWh as a string
 * @param standingChargeRatePDay  standing charge rate in pence per day as a string
 * @param standingChargeTotalGbp  billed standing charge total (£) as a string
 * @param totalCostGbp            total electricity charge including VAT (£) as a string
 * 
 */
```
### Database table
```sql
CREATE TABLE [dbo].[Electricity] (
 *     [BillDate]            DATE           NOT NULL,
 *     [BillPeriodStart]     DATE           NOT NULL,
 *     [BillPeriodEnd]       DATE           NOT NULL,
 *     [TariffName]          VARCHAR(50)    NOT NULL,
 *     [TariffPeriodStart]   DATE           NOT NULL,
 *     [TariffPeriodEnd]     DATE           NOT NULL,
 *     [MPAN]                VARCHAR(50)    NULL,
 *     [MeterID]             VARCHAR(50)    NULL,
 *     [StartReadingDate]    DATE           NOT NULL,
 *     [StartReadingValue]   NUMERIC(6,2)   NOT NULL,
 *     [StartReadingType]    VARCHAR(50)    NULL,
 *     [EndReadingDate]      DATE           NOT NULL,
 *     [EndReadingValue]     NUMERIC(6,2)   NOT NULL,
 *     [EndReadingType]      VARCHAR(50)    NOT NULL,
 *     [EnergyUsed]          NUMERIC(6,2)   NOT NULL,
 *     [UnitRate]            NUMERIC(6,2)   NOT NULL,
 *     [StandingChargeRate]  NUMERIC(6,2)   NOT NULL
 * )
```
### GasRecord
- will need validating
	- all data is string
	- unit rate/standing charge is in pence (100pence=1pound)
	- readings are decimal number
	- consumption is multipled by a facor to convert to kWh
	- totals are in pounds (GBP)
```Java
/**
 * Immutable record holding one row of extracted gas billing data,
 *
 * @param billDate                statement date in {@code yyyy-MM-dd} format
 * @param billPeriodStart         overall bill period start in {@code yyyy-MM-dd}
 * @param billPeriodEnd           overall bill period end in {@code yyyy-MM-dd}
 * @param tariffName              Octopus tariff name
 * @param tariffPeriodStart       tariff sub-period start in {@code yyyy-MM-dd}
 * @param tariffPeriodEnd         tariff sub-period end in {@code yyyy-MM-dd}
 * @param mprn                    Meter Point Reference Number (gas supply point ID)
 * @param meterId                 physical meter serial number
 * @param startReadingDate        opening meter reading date in {@code yyyy-MM-dd}
 * @param startReadingValue       opening meter reading (m³) as a string
 * @param startReadingType        how the opening reading was obtained
 * @param endReadingDate          closing meter reading date in {@code yyyy-MM-dd}
 * @param endReadingValue         closing meter reading (m³) as a string
 * @param endReadingType          how the closing reading was obtained
 * @param consumptionM3           gas consumed in cubic metres as a string
 * @param energyUsedKwh           energy equivalent in kWh as a string; may be negative
 * @param unitRatePKwh            unit rate in pence per kWh as a string
 * @param standingChargeRatePDay  standing charge rate in pence per day as a string
 * @param standingChargeTotalGbp  billed standing charge total (£) as a string
 * @param totalCostGbp            total gas charge including VAT (£) as a string
 *
 */
```
### Database Table
```sql
 * CREATE TABLE [dbo].[Gas] (
 *     [BillDate]            DATE           NOT NULL,
 *     [BillPeriodStart]     DATE           NOT NULL,
 *     [BillPeriodEnd]       DATE           NOT NULL,
 *     [TariffName]          VARCHAR(50)    NULL,
 *     [TariffPeriodStart]   DATE           NOT NULL,
 *     [TariffPeriodEnd]     DATE           NOT NULL,
 *     [MPRN]                VARCHAR(50)    NULL,
 *     [MeterID]             VARCHAR(50)    NULL,
 *     [StartReadingDate]    DATE           NOT NULL,
 *     [StartReadingValue]   NUMERIC(6,2)   NOT NULL,
 *     [StartReadingType]    VARCHAR(50)    NULL,
 *     [EndReadingDate]      DATE           NOT NULL,
 *     [EndReadingValue]     NUMERIC(6,2)   NOT NULL,
 *     [EndReadingType]      VARCHAR(50)    NULL,
 *     [Consumption]         NUMERIC(6,2)   NOT NULL,
 *     [EnergyUsed]          NUMERIC(6,2)   NOT NULL,
 *     [UnitRate]            NUMERIC(6,2)   NOT NULL,
 *     [StandingChargeRate]  NUMERIC(6,2)   NOT NULL
 * )
```