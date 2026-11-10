/*
 * Copyright Â© 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

/** Tests packaged application identity metadata. @version 4.1.0-GUi
 * @author Terry Curran
 */
class ApplicationInfoTest {

    @Test
    void suppliesProductNameAndVersionOutsidePackagedJar() {
        var information = ApplicationInfo.current();
        assertEquals("OpenData", information.productName());
        assertFalse(information.version().isBlank());
        assertEquals("4.0.0", information.version());
    }
}
