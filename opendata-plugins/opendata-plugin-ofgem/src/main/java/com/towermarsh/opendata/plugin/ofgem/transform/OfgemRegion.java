/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem.transform;

import com.towermarsh.opendata.exception.ImportException;
import java.util.Arrays;
import java.util.Locale;

/**
 * Ofgem charge-restriction regions and their stored dimension codes.
 *
 * @author Terry Curran
 * @since 4.2.0
 * @version 4.2.0
 */
public enum OfgemRegion {
    GB_AVERAGE("gb average"),
    NORTH_WEST("north west"),
    NORTHERN("northern"),
    YORKSHIRE("yorkshire"),
    NORTHERN_SCOTLAND("northern scotland"),
    SOUTHERN("southern"),
    SOUTHERN_SCOTLAND("southern scotland"),
    N_WALES_AND_MERSEY("n wales and mersey"),
    LONDON("london"),
    SOUTH_EAST("south east"),
    EASTERN("eastern"),
    EAST_MIDLANDS("east midlands"),
    MIDLANDS("midlands"),
    SOUTHERN_WESTERN("southern western"),
    SOUTH_WALES("south wales");

    private final String label;

    OfgemRegion(String label) {
        this.label = label;
    }

    /**
     * Returns the region dimension code stored by OpenData.
     *
     * @return persisted region code
     */
    public String code() {
        return name();
    }

    /**
     * Resolves a worksheet label, including VAT-annotated GB average rows.
     *
     * @param displayName region label from the worksheet
     * @return matching Ofgem region
     * @throws ImportException if no region matches the label
     */
    public static OfgemRegion fromDisplayName(String displayName) throws ImportException {
        if (displayName == null) {
            throw new ImportException("Unrecognised Ofgem charge-restriction region: null");
        }
        var normalised = displayName.trim().toLowerCase(Locale.ROOT);
        if (normalised.startsWith(GB_AVERAGE.label)) {
            return GB_AVERAGE;
        }
        return Arrays.stream(values())
                .filter(region -> region.label.equals(normalised))
                .findFirst()
                .orElseThrow(() -> new ImportException(
                        "Unrecognised Ofgem charge-restriction region: " + displayName));
    }
}
