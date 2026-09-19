/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package com.towermarsh.opendata.plugin.octopus.load;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.towermarsh.opendata.database.DatabaseAccessException;
import com.towermarsh.opendata.database.DatabaseResourceManager;
import com.towermarsh.opendata.plugin.octopus.extract.ExtractedOctopusStatement;
import com.towermarsh.opendata.plugin.octopus.transform.OctopusParseResult;
import com.towermarsh.opendata.plugin.octopus.transform.model.ElectricityRecord;
import com.towermarsh.opendata.plugin.octopus.transform.model.GasRecord;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Tests Octopus integration with the shared transaction and upsert framework.
 *
 * @author Terry Curran
 * @version 2.0.0
 * @since 2.0.0
 */
class OctopusPersistenceRepositoryTest {

    @Test
    void insertsElectricityAndGasUsingOneTransaction() {
        final var jdbc = new FakeJdbc(false, false, false);
        final var repository = new OctopusPersistenceRepository(jdbc.database());

        final var result = repository.save(batch(), UUID.randomUUID());

        assertEquals(new OctopusPersistenceResult(2, 0, 0), result);
        assertEquals(1, jdbc.electricityInserts);
        assertEquals(1, jdbc.gasInserts);
        assertEquals(1, jdbc.statementMerges);
        assertTrue(jdbc.committed);
        assertFalse(jdbc.rolledBack);
        assertTrue(jdbc.autoCommit);
    }

    @Test
    void updatesExistingElectricityAndGasUsingSameExecutor() {
        final var jdbc = new FakeJdbc(true, true, false);
        final var repository = new OctopusPersistenceRepository(jdbc.database());

        final var result = repository.save(batch(), UUID.randomUUID());

        assertEquals(new OctopusPersistenceResult(0, 2, 0), result);
        assertEquals(1, jdbc.electricityUpdates);
        assertEquals(1, jdbc.gasUpdates);
        assertEquals(1, jdbc.statementMerges);
        assertTrue(jdbc.committed);
    }

    @Test
    void rollsBackAndWrapsSqlFailure() {
        final var jdbc = new FakeJdbc(false, false, true);
        final var repository = new OctopusPersistenceRepository(jdbc.database());

        assertThrows(DatabaseAccessException.class,
                () -> repository.save(batch(), UUID.randomUUID()));

        assertTrue(jdbc.rolledBack);
        assertFalse(jdbc.committed);
        assertTrue(jdbc.autoCommit);
    }

    private static OctopusParseResult batch() {
        return new OctopusParseResult(
                List.of(electricity()),
                List.of(gas()),
                List.of(new ExtractedOctopusStatement(
                        Path.of("octopus-energy-statement-2026-07-01.pdf"),
                        "octopus-energy-statement-2026-07-01.pdf",
                        LocalDate.of(2026, 7, 1),
                        "abc123",
                        1024,
                        "statement text")));
    }

    private static ElectricityRecord electricity() {
        return new ElectricityRecord(
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30),
                "Flexible Octopus",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30),
                "2000012845052",
                "ELECTRIC-1",
                LocalDate.of(2026, 6, 1),
                new BigDecimal("100.0"),
                "Smart meter reading",
                LocalDate.of(2026, 6, 30),
                new BigDecimal("150.0"),
                "Smart meter reading",
                new BigDecimal("50.0"),
                new BigDecimal("24.5"),
                new BigDecimal("60.0"),
                new BigDecimal("18.0"),
                new BigDecimal("30.25"));
    }

    private static GasRecord gas() {
        return new GasRecord(
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30),
                "Flexible Octopus",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30),
                "3343444302",
                "GAS-1",
                LocalDate.of(2026, 6, 1),
                new BigDecimal("100.0"),
                "Smart meter reading",
                LocalDate.of(2026, 6, 30),
                new BigDecimal("110.0"),
                "Smart meter reading",
                new BigDecimal("10.0"),
                new BigDecimal("110.0"),
                new BigDecimal("6.5"),
                new BigDecimal("31.0"),
                new BigDecimal("9.3"),
                new BigDecimal("16.45"));
    }

    private static final class FakeJdbc {

        private final boolean electricityExists;
        private final boolean gasExists;
        private final boolean fail;
        private boolean autoCommit = true;
        private boolean committed;
        private boolean rolledBack;
        private int electricityInserts;
        private int electricityUpdates;
        private int gasInserts;
        private int gasUpdates;
        private int statementMerges;

        FakeJdbc(
                final boolean electricityExists,
                final boolean gasExists,
                final boolean fail) {
            this.electricityExists = electricityExists;
            this.gasExists = gasExists;
            this.fail = fail;
        }

        DatabaseResourceManager database() {
            return new DatabaseResourceManager() {
                @Override
                public Connection getConnection() {
                    return connection();
                }

                @Override
                public void close() {
                    // Nothing to release in the test fake.
                }
            };
        }

        private Connection connection() {
            return proxy(Connection.class, this::invokeConnection);
        }

        private Object invokeConnection(
                final Object proxy,
                final Method method,
                final Object[] arguments) throws SQLException {
            return switch (method.getName()) {
                case "getAutoCommit" -> autoCommit;
                case "setAutoCommit" -> {
                    autoCommit = (boolean) arguments[0];
                    yield null;
                }
                case "prepareStatement" -> preparedStatement((String) arguments[0]);
                case "commit" -> {
                    committed = true;
                    yield null;
                }
                case "rollback" -> {
                    rolledBack = true;
                    yield null;
                }
                case "close" -> null;
                case "isClosed" -> false;
                case "unwrap" -> null;
                case "isWrapperFor" -> false;
                default -> defaultValue(method.getReturnType());
            };
        }

        private PreparedStatement preparedStatement(final String sql) {
            final Map<Integer, Object> parameters = new HashMap<>();
            return proxy(PreparedStatement.class, (proxy, method, arguments) -> {
                final String methodName = method.getName();
                if (methodName.startsWith("set") && arguments != null
                        && arguments.length >= 2 && arguments[0] instanceof Integer index) {
                    parameters.put(index, arguments[1]);
                    return null;
                }
                return switch (methodName) {
                    case "executeQuery" -> executeQuery(sql);
                    case "executeUpdate" -> executeUpdate(sql);
                    case "close", "clearParameters" -> null;
                    case "isClosed" -> false;
                    case "unwrap" -> null;
                    case "isWrapperFor" -> false;
                    default -> defaultValue(method.getReturnType());
                };
            });
        }

        private ResultSet executeQuery(final String sql) throws SQLException {
            if (fail) {
                throw new SQLException("simulated Octopus SQL failure");
            }
            final boolean exists;
            if (sql.contains("octopus.electric_data")) {
                exists = electricityExists;
            } else if (sql.contains("octopus.gas_data")) {
                exists = gasExists;
            } else {
                exists = false;
            }
            return resultSet(exists);
        }

        private int executeUpdate(final String sql) throws SQLException {
            if (fail) {
                throw new SQLException("simulated Octopus SQL failure");
            }
            if (sql.startsWith("INSERT INTO octopus.electric_data")) {
                electricityInserts++;
            } else if (sql.startsWith("UPDATE octopus.electric_data")) {
                electricityUpdates++;
            } else if (sql.startsWith("INSERT INTO octopus.gas_data")) {
                gasInserts++;
            } else if (sql.startsWith("UPDATE octopus.gas_data")) {
                gasUpdates++;
            } else if (sql.startsWith("MERGE octopus.statement_file")) {
                statementMerges++;
            }
            return 1;
        }

        private static ResultSet resultSet(final boolean value) {
            final boolean[] first = {true};
            return proxy(ResultSet.class, (proxy, method, arguments) -> switch (method.getName()) {
                case "next" -> {
                    final boolean result = first[0] && value;
                    first[0] = false;
                    yield result;
                }
                case "close" -> null;
                case "isClosed" -> false;
                case "unwrap" -> null;
                case "isWrapperFor" -> false;
                default -> defaultValue(method.getReturnType());
            });
        }

        private static Object defaultValue(final Class<?> type) {
            if (!type.isPrimitive()) {
                return null;
            }
            if (type == boolean.class) {
                return false;
            }
            if (type == byte.class) {
                return (byte) 0;
            }
            if (type == short.class) {
                return (short) 0;
            }
            if (type == int.class) {
                return 0;
            }
            if (type == long.class) {
                return 0L;
            }
            if (type == float.class) {
                return 0.0F;
            }
            if (type == double.class) {
                return 0.0D;
            }
            if (type == char.class) {
                return '\0';
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        private static <T> T proxy(
                final Class<T> type,
                final InvocationHandler handler) {
            return (T) Proxy.newProxyInstance(
                    type.getClassLoader(),
                    new Class<?>[]{type},
                    handler);
        }
    }
}
