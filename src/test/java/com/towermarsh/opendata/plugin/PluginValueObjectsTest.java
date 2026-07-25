package com.towermarsh.opendata.plugin;

import static org.junit.jupiter.api.Assertions.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;

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
