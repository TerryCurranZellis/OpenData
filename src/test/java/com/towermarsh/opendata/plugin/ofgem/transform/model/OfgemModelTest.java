package com.towermarsh.opendata.plugin.ofgem.transform.model;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class OfgemModelTest {
 @Test void periodTrimsName() { var p=new OfgemPriceCapPeriod(" Jul-Sep 2026 ",LocalDate.of(2026,7,1),LocalDate.of(2026,9,30),12,true); assertEquals("Jul-Sep 2026",p.periodName()); }
 @Test void periodRejectsReverseDates() { assertThrows(IllegalArgumentException.class,()->new OfgemPriceCapPeriod("x",LocalDate.of(2026,2,1),LocalDate.of(2026,1,1),1,false)); }
 @Test void periodRejectsNonPositiveColumn() { assertThrows(IllegalArgumentException.class,()->new OfgemPriceCapPeriod("x",LocalDate.now(),LocalDate.now(),0,false)); }
 @Test void levelAcceptsZero() { assertEquals(BigDecimal.ZERO,new OfgemPriceCapLevel("GB","DD","SVT","TYPICAL",BigDecimal.ZERO,true,"Sheet","A1").amountGbp()); }
 @Test void levelRejectsNegativeAmount() { assertThrows(IllegalArgumentException.class,()->new OfgemPriceCapLevel("GB","DD","SVT","TYPICAL",BigDecimal.valueOf(-1),true,"Sheet","A1")); }
}
