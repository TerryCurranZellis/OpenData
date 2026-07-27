/*
 * Filename: OfgemModelTest.java
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
package com.towermarsh.opendata.plugin.ofgem.transform.model;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/**
 * @author Terry Curran
 * @version 17 July 2026
 */
class OfgemModelTest {
 @Test void periodTrimsName() { var p=new OfgemPriceCapPeriod(" Jul-Sep 2026 ",LocalDate.of(2026,7,1),LocalDate.of(2026,9,30),12,true); assertEquals("Jul-Sep 2026",p.periodName()); }
 @Test void periodRejectsReverseDates() { assertThrows(IllegalArgumentException.class,()->new OfgemPriceCapPeriod("x",LocalDate.of(2026,2,1),LocalDate.of(2026,1,1),1,false)); }
 @Test void periodRejectsNonPositiveColumn() { assertThrows(IllegalArgumentException.class,()->new OfgemPriceCapPeriod("x",LocalDate.now(),LocalDate.now(),0,false)); }
 @Test void levelAcceptsZero() { assertEquals(BigDecimal.ZERO,new OfgemPriceCapLevel("GB","DD","SVT","TYPICAL",BigDecimal.ZERO,true,"Sheet","A1").amountGbp()); }
 @Test void levelRejectsNegativeAmount() { assertThrows(IllegalArgumentException.class,()->new OfgemPriceCapLevel("GB","DD","SVT","TYPICAL",BigDecimal.valueOf(-1),true,"Sheet","A1")); }
}
