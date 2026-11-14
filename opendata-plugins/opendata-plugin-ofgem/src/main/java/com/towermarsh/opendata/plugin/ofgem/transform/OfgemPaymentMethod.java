/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.ofgem.transform;

/**
 * Workbook payment-method headings and their persisted database codes.
 *
 * <p>Declaration order determines the order in which sections are read.</p>
 *
 * @author Terry Curran
 * @since 4.2.0
 * @version 4.2.0
 */
public enum OfgemPaymentMethod {
    OTHER("Other Payment Method", "OTHER"),
    STANDARD_CREDIT("Standard Credit", "STANDARD_CREDIT"),
    PPM("PPM", "PPM");

    private final String heading;
    private final String code;

    OfgemPaymentMethod(String heading, String code) {
        this.heading = heading;
        this.code = code;
    }

    /**
     * Returns the section heading in the Ofgem workbook.
     *
     * @return section heading
     */
    public String heading() {
        return heading;
    }

    /**
     * Returns the database payment-method code.
     *
     * @return persisted payment-method code
     */
    public String code() {
        return code;
    }
}
