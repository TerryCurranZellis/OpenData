/*
 * Copyright © 2026 Terry Curran
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopusadjustment.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests adjustment transform batch behaviour that does not require a PDF fixture. */
class OctopusAdjustmentTransformTest {

    @Test
    void emptySourceBatchProducesEmptyResult() throws Exception {
        final var result = new OctopusAdjustmentTransform().transform(List.of());

        assertEquals(0, result.totalRecords());
        assertEquals(0, result.sources().size());
        assertEquals(0, result.electricityRecords().size());
        assertEquals(0, result.gasRecords().size());
    }
}
