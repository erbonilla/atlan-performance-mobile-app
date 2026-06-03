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

## Drain engine + background workers (in progress)
- **Active-session state** persists for resume + crash recovery (`sessionProgress` table,
  `SessionProgressRepository`, Resume/Discard dashboard banner).
- **Workout History** persists finished sessions (`completedSession` table,
  `WorkoutHistoryRepository`).
- **Drain engine:** `DrainSyncQueueUseCase` hands each pending item to a `SyncUploader` and marks the
  accepted ones synced; unaccepted items stay safely pending (never dropped). The only uploader this
  milestone is `SimulatedSyncUploader` (no backend) — the seam where a real HTTP uploader drops in.
- **Background workers:** Android `SyncWorker` (`CoroutineWorker`, scheduled with a `CONNECTED`
  constraint on launch) opens the same DB and runs the drain. iOS drains when the app becomes active
  (`scenePhase`); `BackgroundSync` (BGTaskScheduler) is the documented background path, not yet
  registered (needs an Info.plist identifier — see its TODO). The Wet Mode summary's Retry also runs
  the real engine in the foreground.

## TODOs (next)
- A **real remote API** behind `SyncUploader` (replacing `SimulatedSyncUploader`) — the last piece to
  make sync truly live; then enable the iOS BGTask path (Info.plist identifier).
- Offload DB calls to a background dispatcher (add `kotlinx-coroutines-core` to the shared module).
- Conflict resolution (deterministic).
- Auth; encrypted local storage for sensitive user settings.
- (Seed-backed read-only repos — plan/why/profile defaults — stay in-memory until a backend hydrates
  them; user-generated data already lives in SQLDelight.)
