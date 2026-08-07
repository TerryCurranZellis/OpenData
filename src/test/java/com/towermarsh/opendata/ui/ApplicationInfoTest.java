/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

/** Tests packaged application identity metadata. @version 2.1 */
class ApplicationInfoTest {

    @Test
    void suppliesProductNameAndVersionOutsidePackagedJar() {
        final var information = ApplicationInfo.current();
        assertEquals("OpenData", information.productName());
        assertFalse(information.version().isBlank());
        assertEquals("2.1.0", information.version());
    }
}
