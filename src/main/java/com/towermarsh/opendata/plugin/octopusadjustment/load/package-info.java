/**
 * Persistence support for Octopus Energy adjustment records.
 *
 * <p>The load stage is specific to adjustment data and writes only to the
 * adjustment electricity, gas and processed-file tables. Business and ledger
 * writes share one transaction.</p>
 *
 * @since 3.1.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.load;
