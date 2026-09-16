/*
 * Copyright Â© 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

/** Tests packaged application identity metadata. @version 3.0.0-GUi */
class ApplicationInfoTest {

    @Test
    void suppliesProductNameAndVersionOutsidePackagedJar() {
        final var information = ApplicationInfo.current();
        assertEquals("OpenData", information.productName());
        assertFalse(information.version().isBlank());
        assertEquals("3.0.0", information.version());
    }
}
