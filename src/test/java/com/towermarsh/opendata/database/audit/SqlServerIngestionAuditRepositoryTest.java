/*
 * Filename: SqlServerIngestionAuditRepositoryTest.java
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
package com.towermarsh.opendata.database.audit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import com.towermarsh.opendata.database.DatabaseConnectionManager;
import com.towermarsh.opendata.database.DatabaseException;
import java.net.URI;
import java.sql.*;
import java.time.Instant;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension./**
 * @author Terry Curran
 * @version 17 July 2026
 */
class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SqlServerIngestionAuditRepositoryTest {
 

 @Mock DatabaseConnectionManager manager; @Mock(lenient = true) Connection connection; @Mock PreparedStatement statement; @Mock ResultSet keys;
 SqlServerIngestionAuditRepository repo;
 @BeforeEach void setup() throws Exception { repo=new SqlServerIngestionAuditRepository(manager); when(manager.getConnection()).thenReturn(connection); when(connection.prepareStatement(anyString(),anyInt())).thenReturn(statement); when(connection.prepareStatement(anyString())).thenReturn(statement); when(statement.getGeneratedKeys()).thenReturn(keys); }
 @Test void startsRunAndReturnsGeneratedKey() throws Exception { when(statement.executeUpdate()).thenReturn(1); when(keys.next()).thenReturn(true); when(keys.getLong(1)).thenReturn(41L); assertEquals(41L,repo.startRun("ofgem", URI.create("https://ofgem.gov.uk"),"1.0")); verify(statement).setString(4,"ofgem"); }
 @Test void rejectsUnknownDataset() throws Exception { when(statement.executeUpdate()).thenReturn(0); assertThrows(DatabaseException.class,()->repo.startRun("missing",null,"1.0")); }
 @Test void rejectsMissingGeneratedKey() throws Exception { when(statement.executeUpdate()).thenReturn(1); when(keys.next()).thenReturn(false); assertThrows(DatabaseException.class,()->repo.startRun("ofgem",null,"1.0")); }
 @Test void completesRun() throws Exception { when(statement.executeUpdate()).thenReturn(1); var c=new IngestionRunCompletion(IngestionStatus.SUCCEEDED,10,9,1,Instant.parse("2026-07-25T12:00:00Z"),"done"); repo.completeRun(7,c); verify(statement).setLong(7,7); }
 @Test void completeRejectsNonStartedRun() throws Exception { when(statement.executeUpdate()).thenReturn(0); var c=new IngestionRunCompletion(IngestionStatus.FAILED,0,0,0,Instant.now(),"bad"); assertThrows(DatabaseException.class,()->repo.completeRun(7,c)); }
 @Test void recordErrorUsesSqlNulls() throws Exception { repo.recordError(1,null,null,"LOAD","SQL","bad",null); verify(statement).setNull(2,Types.BIGINT); verify(statement).setNull(3,Types.BIGINT); }
}
