IF SCHEMA_ID('electricsupply') IS NULL EXEC('CREATE SCHEMA electricsupply');
GO
IF OBJECT_ID('electricsupply.GenerationMix','U') IS NULL
CREATE TABLE electricsupply.GenerationMix (
 DateTimeUtc datetime2(0) NOT NULL,
 GasMW decimal(18,3) NULL, CoalMW decimal(18,3) NULL, NuclearMW decimal(18,3) NULL,
 WindMW decimal(18,3) NULL,EmbeddedWindMW decimal(18,3) NULL,HydroMW decimal(18,3) NULL,
 ImportsMW decimal(18,3) NULL,BiomassMW decimal(18,3) NULL,OtherMW decimal(18,3) NULL,
 SolarMW decimal(18,3) NULL,StorageMW decimal(18,3) NULL,GenerationMW decimal(18,3) NULL,
 CarbonIntensity decimal(18,3) NULL,
 CONSTRAINT PK_ElectricSupply PRIMARY KEY (DateTimeUtc));
GO
