# Offline-First — Atlan Performance

## Core rule
> Local data is the app's source of truth. Network sync is secondary. The app must be able to read
> today's session, start Wet Mode, complete sets, and view cached Why content without connectivity.

## Data flow
```
UI action
  -> platform UI event
  -> shared KMP use case
  -> local write (source of truth)
  -> UI updates immediately from local state
  -> sync queue item created
  -> platform background worker drains queue later
  -> remote success updates local sync status
```
Network must never block Wet Mode. Workout completion writes locally first.

## Initial implementation
- Fake in-memory repositories, seeded on app launch (`data/seed/`, `data/fake/`) — for profile,
  plan, session, Why, and swap.
- Today's session is seeded with `offlineAvailable = true`.
- Sync queue **types** are modeled (`data/sync/SyncQueueItem`, `SyncOperation`, `SyncState`).
- Repository interfaces (`domain/repository/`) are designed so fakes can be replaced by real
  local-database repositories without changing use cases.

## Persistence layer (in progress)
- **SQLDelight is wired into the shared module** (`app.cash.sqldelight`, version catalog + shared
  `build.gradle.kts`; declared at the root with `apply false` so it can't downgrade the Kotlin Gradle
  plugin). Schema lives in `shared/src/commonMain/sqldelight/.../SyncQueue.sq`; the generated DB is
  `AtlanDatabase`.
- A platform driver is provided via `expect/actual DatabaseDriverFactory`: Android
  `AndroidSqliteDriver(context)`, iOS `NativeSqliteDriver`, JVM `JdbcSqliteDriver(IN_MEMORY)` (so
  persistence is testable with only a JDK). iOS links system `libsqlite3` (`-lsqlite3` in
  `project.yml`).
- The **sync queue is the first repository migrated to durable storage**
  (`data/local/SqlDelightSyncQueueRepository`, replacing the fake in `AtlanShared`). Rows survive
  process death; `markSynced` is the drain primitive. Proven by `SyncQueuePersistenceTest`
  (jvmTest): an item is readable from a second repository instance on the same DB.
- `AtlanShared(databaseDriverFactory)` now takes a driver; Android passes
  `DatabaseDriverFactory(applicationContext)`, iOS `DatabaseDriverFactory()`.

## TODOs (next)
- Migrate the remaining repositories (profile/plan/session) to SQLDelight, following the sync-queue
  pattern; persist active-session/timer state for resume + crash recovery.
- Background sync drain (WorkManager on Android; BackgroundTasks on iOS) + a real remote API.
- Offload DB calls to a background dispatcher (add `kotlinx-coroutines-core` to the shared module).
- Conflict resolution (deterministic).
- Auth.
- Encrypted local storage for sensitive user settings.
