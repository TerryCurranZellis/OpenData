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

@ExtendWith(MockitoExtension.class)
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
