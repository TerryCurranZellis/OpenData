/*
 *  Filename: RawElectricityRecord.java
 *
 *  (C) Copyright Terry Curran 2026. All rights reserved
 *
 *  This software is provided 'as-is', without any express or implied
 *  warranty.  In no event will the author be held liable for any damages
 *  arising from the use of this software.
 *
 *  Permission is granted to anyone to use this software for any purpose,
 *  including commercial applications, and to alter it and redistribute it
 *  freely, subject to the following restrictions:
 *
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software
 *     in a product, an acknowledgement in the product documentation would be
 *     appreciated but is not required.
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 *  3. This notice may not be removed or altered from any source distribution.
 *
 *  The author may be contacted by email to the following address:
 *
 *  terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.plugin.octopus.extract;

/**
 *
 * Immutable record holding one row of extracted electricity billing data,
 * matching the column layout of {@code electric_data.csv}.
 *
 * <p>All fields are stored as {@code String} values (empty string when absent)
 * so they can be written directly to CSV without conversion or mapped into SQL
 * parameters by the DAO layer. Downstream analysis can then perform whatever
 * numeric conversions it requires.
 *
 * <h2>Example</h2>
 * <pre>
 *   RawElectricityRecord r = new RawElectricityRecord(
 *       "2022-01-05", "2021-12-07", "2022-01-05",
 *       "Octopus Exclusive 12M Fixed", "2021-12-04", "2022-01-03",
 *       "2000012845052", "20E5013326",
 *       "2021-12-04", "2257.0", "Smart meter reading",
 *       "2022-01-04", "2481.3", "Smart meter reading",
 *       "224.3", "15.51", "19.23", "5.96", "42.78");
 * </pre>
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
 * @author (C) Copyright Terry Curran 2026. All Rights Reserved.
 * @version 15 Mar 2026
 */
public record RawElectricityRecord(
        String billDate,
        String billPeriodStart,
        String billPeriodEnd,
        String tariffName,
        String tariffPeriodStart,
        String tariffPeriodEnd,
        String mpan,
        String meterId,
        String startReadingDate,
        String startReadingValue,
        String startReadingType,
        String endReadingDate,
        String endReadingValue,
        String endReadingType,
        String energyUsedKwh,
        String unitRatePKwh,
        String standingChargeRatePDay,
        String standingChargeTotalGbp,
        String totalCostGbp) {
}
