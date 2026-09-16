/*
 * Copyright © 2026 Terry Curran
 *
 * SPDX-License-Identifier: Apache-2.0
 */
/**
 * Database-backed ingestion audit and source provenance support.
 *
 * <h2>Classes</h2>
 * <ul>
 * <li>{@link SqlServerIngestionAuditRepository} &mdash; SQL Server implementation of the framework ingestion audit repository.</li>
 * </ul>
 *
 * <h2>Records</h2>
 * <ul>
 * <li>{@link IngestionRunCompletion} &mdash; Final counters and status for an ingestion run.</li>
 * <li>{@link SourceFileMetadata} &mdash; Audit table metadata for a downloaded source file.</li>
 * </ul>
 *
 * <h2>Interfaces</h2>
 * <ul>
 * <li>{@link IngestionAuditRepository} &mdash; Persists the shared ingestion audit trail.</li>
 * </ul>
 *
 * <h2>Enums</h2>
 * <ul>
 * <li>{@link IngestionStatus} &mdash; Persisted lifecycle status for one dataset ingestion run.</li>
 * </ul>
 *
 * @author Terry Curran
 * @version 3.0.0
 */
package com.towermarsh.opendata.database.audit;
