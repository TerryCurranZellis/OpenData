# Database concurrency and idempotency

## Connection ownership

`SQLServerResource` owns one Apache Commons DBCP pool. The pool is shared; JDBC connections are not. Each plugin repository borrows a connection, starts and completes its own transaction, then closes the connection so it returns to the pool.

The configured `database.pool.max-total` should be at least the maximum parallel plugin count plus a small allowance for audit operations. The supplied defaults are eight connections and four concurrent plugins.

## Synchronisation boundaries

No JVM-wide lock surrounds plugin execution or database access. Java `synchronized` would only coordinate one process and would unnecessarily serialize independent datasets.

Coordination belongs at the narrowest shared resource:

- primary/unique keys prevent duplicate logical rows;
- transactions make each load atomic;
- `UPDLOCK, HOLDLOCK` protects key ranges during update/insert decisions;
- OpenMeteo obtains `sp_getapplock` on `OpenData:openmeteo:<location-key>` with `LockOwner='Transaction'`.

The location-scoped application lock permits different locations and other plugins to run concurrently while preventing overlapping loads of the same OpenMeteo location, even from separate JVM processes. SQL Server releases a transaction-owned application lock on commit or rollback.

## Why not `MERGE`

The repository stages source records into a temporary table, updates changed targets, then inserts missing targets. This is explicit, auditable and avoids depending on `MERGE` for concurrency correctness.

## Transaction sequence

1. Borrow pooled connection.
2. Disable auto-commit and set `XACT_ABORT ON`.
3. Acquire location application lock.
4. Upsert the location dimension under update/range locks.
5. Create and populate `#OpenMeteoDaily`.
6. Update changed daily rows.
7. Insert missing dates under range locks.
8. Commit; the application lock is released.
9. Drop the connection-scoped temporary table, restore `XACT_ABORT`, and commit that cleanup.
10. Restore the original auto-commit setting and return the connection to the pool.

Any exception rolls back the whole data transaction. The cleanup step is important with a connection pool: a logical `Connection.close()` returns the physical SQL Server session to DBCP, so local temporary tables and session `SET` options must not be allowed to leak into the next borrower.

## Pool sizing

A practical starting rule is:

```text
max-total >= max-parallel-plugins + 2
```

Raise it only after measuring database load and connection wait time. More connections do not automatically improve throughput.

## References

- SQL Server `sp_getapplock`: https://learn.microsoft.com/en-us/sql/relational-databases/system-stored-procedures/sp-getapplock-transact-sql
- Apache Commons DBCP: https://commons.apache.org/proper/commons-dbcp/
