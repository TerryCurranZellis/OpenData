/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.openmeteo.load;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import com.towermarsh.opendata.database.*;
import com.towermarsh.opendata.plugin.openmeteo.config.OpenMeteoConfiguration;
import com.towermarsh.opendata.plugin.openmeteo.transform.model.DailyWeatherRecord;
import java.net.URI;
import java.sql.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension./**
 * @author Terry Curran
 * @version 1.0.0
 */
class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OpenMeteoRepositoryTest {
 @Mock DatabaseResourceManager database; @Mock Connection connection; @Mock PreparedStatement statement; @Mock ResultSet resultSet; @Mock Statement sqlStatement;
 OpenMeteoRepository repository; OpenMeteoConfiguration configuration;
 @BeforeEach void setup() throws Exception {
  repository=new OpenMeteoRepository(database); configuration=new OpenMeteoConfiguration(URI.create("https://x"),"home","Home",51.6,-1.1,ZoneId.of("Europe/London"),Duration.ofSeconds(1),Duration.ofSeconds(2),Optional.empty(),Optional.empty(),365,false,"openmeteo","Location","DailyWeather",2,Duration.ofSeconds(3));
  when(database.getConnection()).thenReturn(connection); when(connection.getAutoCommit()).thenReturn(true); when(connection.prepareStatement(anyString())).thenReturn(statement); when(statement.executeQuery()).thenReturn(resultSet);
  when(connection.createStatement()).thenReturn(sqlStatement); when(sqlStatement.execute(anyString())).thenReturn(true);
  when(resultSet.next()).thenReturn(true); when(resultSet.getInt(1)).thenReturn(0); when(resultSet.getLong(1)).thenReturn(10L);
 }
 private DailyWeatherRecord record(int day){ return new DailyWeatherRecord(LocalDate.of(2026,7,day),"Home",51.6,-1.1,10,20,15,LocalTime.of(5,0),LocalTime.of(21,0),960,1,"Clear"); }
 @Test void emptyInputAvoidsConnection() { assertEquals(new OpenMeteoPersistenceResult(0,0,0),repository.save(configuration,List.of(),UUID.randomUUID())); verifyNoInteractions(database); }
 @Test void persistsAndCommits() throws Exception { when(statement.executeUpdate()).thenReturn(1,1,0); var r=repository.save(configuration,List.of(record(1)),UUID.randomUUID()); assertEquals(1,r.inserted()+r.updated()+r.skipped()); verify(connection).commit(); verify(connection).setAutoCommit(false); verify(connection).setAutoCommit(true); }
 @Test void batchesAccordingToConfiguration() throws Exception { when(statement.executeUpdate()).thenReturn(1,1,0,3); repository.save(configuration,List.of(record(1),record(2),record(3)),UUID.randomUUID()); verify(statement,atLeast(2)).executeBatch(); }
 @Test void sqlFailureRollsBackAndWraps() throws Exception { when(connection.prepareStatement(anyString())).thenThrow(new SQLException("broken")); assertThrows(DatabaseAccessException.class,()->repository.save(configuration,List.of(record(1)),UUID.randomUUID())); verify(connection).rollback(); }
}
