package com.towermarsh.opendata.ofgem.database;


import com.towermarsh.opendata.database.DatabaseConnectionManager;
import com.towermarsh.opendata.ofgem.model.OfgemPriceCapLevel;
import com.towermarsh.opendata.ofgem.model.OfgemPriceCapPeriod;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SqlServerOfgemPriceCapRepositoryTest {
 @Mock DatabaseConnectionManager manager; @Mock Connection connection; @Mock PreparedStatement statement; @Mock ResultSet resultSet;
 SqlServerOfgemPriceCapRepository repo;
 @BeforeEach void setup() throws Exception { repo=new SqlServerOfgemPriceCapRepository(manager); when(manager.getConnection()).thenReturn(connection); when(connection.getAutoCommit()).thenReturn(true); when(connection.prepareStatement(anyString())).thenReturn(statement); when(connection.prepareStatement(anyString(),eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(statement); }
 private OfgemPriceCapPeriod period(boolean current){ return new OfgemPriceCapPeriod("Jul-Sep 2026",LocalDate.of(2026,7,1),LocalDate.of(2026,9,30),12,current); }
 private OfgemPriceCapLevel level(){ return new OfgemPriceCapLevel("Eastern","DIRECT_DEBIT","SVT","TYPICAL",new BigDecimal("1755.00"),true,"1a Levelised DTC","M20"); }
 @Test void insertsNewPeriodAndCommits() throws Exception { when(statement.executeQuery()).thenReturn(resultSet); when(resultSet.next()).thenReturn(false,true); when(statement.getGeneratedKeys()).thenReturn(resultSet); when(resultSet.getLong(1)).thenReturn(99L); assertEquals(99L,repo.upsertPeriod(period(true),3)); verify(connection).commit(); verify(connection,never()).rollback(); }
 @Test void updatesExistingPeriod() throws Exception { when(statement.executeQuery()).thenReturn(resultSet); when(resultSet.next()).thenReturn(true); when(resultSet.getLong(1)).thenReturn(44L); assertEquals(44L,repo.upsertPeriod(period(false),3)); verify(statement,atLeastOnce()).executeUpdate(); verify(connection).commit(); }
 @Test void rollsBackPeriodFailure() throws Exception { when(statement.executeQuery()).thenThrow(new SQLException("db")); assertThrows(SQLException.class,()->repo.upsertPeriod(period(false),3)); verify(connection).rollback(); }
 @Test void replacesLevelsAndCountsBatchResults() throws Exception { when(statement.executeBatch()).thenReturn(new int[]{1,Statement.SUCCESS_NO_INFO}); assertEquals(2,repo.replaceLevels(4,8,List.of(level(),level()))); verify(statement,times(2)).addBatch(); verify(connection).commit(); }
 @Test void failedBatchRollsBack() throws Exception { when(statement.executeBatch()).thenReturn(new int[]{Statement.EXECUTE_FAILED}); assertThrows(SQLException.class,()->repo.replaceLevels(4,8,List.of(level()))); verify(connection).rollback(); }
 @Test void emptyLevelsRejectedBeforeDatabaseAccess() { assertThrows(IllegalArgumentException.class,()->repo.replaceLevels(1,2,List.of())); }
}
