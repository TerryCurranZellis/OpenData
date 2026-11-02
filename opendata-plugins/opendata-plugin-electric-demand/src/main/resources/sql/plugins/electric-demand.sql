IF SCHEMA_ID('electricdemand') IS NULL EXEC('CREATE SCHEMA electricdemand');
GO
IF OBJECT_ID('electricdemand.Demand','U') IS NULL
CREATE TABLE electricdemand.Demand (
 SettlementDate date NOT NULL,SettlementPeriod tinyint NOT NULL,
 NationalDemandMW decimal(18,3) NULL,TransmissionSystemDemandMW decimal(18,3) NULL,
 EnglandWalesDemandMW decimal(18,3) NULL,EmbeddedWindMW decimal(18,3) NULL,
 EmbeddedSolarMW decimal(18,3) NULL,PumpingMW decimal(18,3) NULL,
 CONSTRAINT PK_ElectricDemand PRIMARY KEY (SettlementDate,SettlementPeriod),
 CONSTRAINT CK_ElectricDemand_Period CHECK (SettlementPeriod BETWEEN 1 AND 50));
GO

IF DATABASE_PRINCIPAL_ID(N'opendata_app') IS NOT NULL
    GRANT SELECT, INSERT, UPDATE, DELETE ON SCHEMA::[electricdemand] TO [opendata_app];
GO
