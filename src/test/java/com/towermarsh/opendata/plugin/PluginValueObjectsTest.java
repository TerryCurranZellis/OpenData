/*
 * Filename: PluginValueObjectsTest.java
 *
 * (c) Copyright 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * The author may be contacted by email to the following address:
 *
 * terry.curran@towermarsh.co.uk
 */
package com.towermarsh.opendata.plugin;

import static org.junit.jupiter.api.Assertions.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;

/**
 * @author Terry Curran
 * @version 17 July 2026
 */
class PluginValueObjectsTest {
 @Test void metricsRejectNegativeCounts() { assertThrows(IllegalArgumentException.class,()->new PluginMetrics(-1,0,0,0)); }
 @Test void summaryCountsSuccessAndFailure() {
   var now=Instant.parse("2026-07-25T12:00:00Z");
   var ok=new PluginRunResult("a",UUID.randomUUID(),PluginRunStatus.SUCCESS,now,now,new PluginMetrics(1,1,0,0),Optional.empty());
   var fail=new PluginRunResult("b",UUID.randomUUID(),PluginRunStatus.FAILED,now,now,PluginMetrics.ZERO,Optional.of("bad"));
   var s=new PluginExecutionSummary(List.of(ok,fail));
   assertEquals(2,s.results().size()); assertEquals(1,s.succeeded()); assertEquals(1,s.failed());
 }
 @Test void successfulIncludesDryRun() {
   var now=Instant.now();
   assertTrue(new PluginRunResult("a",UUID.randomUUID(),PluginRunStatus.DRY_RUN,now,now,PluginMetrics.ZERO,Optional.empty()).successful());
 }
}
