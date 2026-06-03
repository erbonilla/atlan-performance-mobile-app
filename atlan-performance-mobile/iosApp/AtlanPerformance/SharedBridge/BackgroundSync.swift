import BackgroundTasks

/// Background path for draining the offline sync queue (the iOS counterpart to Android's WorkManager
/// `SyncWorker`). It is intentionally **not wired at launch yet**: `BGTaskScheduler.register` throws
/// unless the identifier is also listed under `BGTaskSchedulerPermittedIdentifiers` in Info.plist,
/// and this milestone has no backend to sync to. The foreground drain (App `scenePhase == .active`)
/// covers draining for now.
///
/// TODO(enable): add `taskId` to Info.plist `BGTaskSchedulerPermittedIdentifiers`, call
/// `register(drain:)` once at launch, and `schedule()` after each session completes.
enum BackgroundSync {
    static let taskId = "com.atlan.performance.ios.syncdrain"

    static func register(drain: @escaping () async -> Void) {
        BGTaskScheduler.shared.register(forTaskWithIdentifier: taskId, using: nil) { task in
            let work = Task { await drain(); task.setTaskCompleted(success: true) }
            task.expirationHandler = { work.cancel() }
        }
    }

    static func schedule() {
        let request = BGAppRefreshTaskRequest(identifier: taskId)
        request.earliestBeginDate = Date(timeIntervalSinceNow: 15 * 60)
        try? BGTaskScheduler.shared.submit(request)
    }
}
