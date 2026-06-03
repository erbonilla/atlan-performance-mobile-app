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
- Fake in-memory repositories, seeded on app launch (`data/seed/`, `data/fake/`).
- Today's session is seeded with `offlineAvailable = true`.
- Sync queue **types** are modeled (`data/sync/SyncQueueItem`, `SyncOperation`, `SyncState`) but no
  backend is connected.
- Repository interfaces (`domain/repository/`) are designed so fakes can be replaced by real
  local-database repositories without changing use cases.

## TODOs (not in initial scope — extension points only)
- SQLDelight or platform-specific database adapters (Room on Android; SwiftData/Core Data/SQLite on iOS).
- Background sync (WorkManager on Android; BackgroundTasks on iOS).
- Conflict resolution (deterministic).
- Remote API integration.
- Auth.
- Encrypted local storage for sensitive user settings.
