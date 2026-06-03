import Foundation
import Shared

/// Single bridge to the Kotlin Multiplatform shared core.
///
/// The shared module is built into a framework named `Shared` (see shared/build.gradle.kts) and
/// embedded by the Xcode project. Kotlin `suspend` functions are exported as Swift `async`; this
/// container centralizes those calls so any generated-signature adjustments live in one place.
///
/// Xcode integration (one-time): add a "Run Script" build phase that runs
///   `./gradlew :shared:embedAndSignAppleFrameworkForXcode`
/// and add `$(SRCROOT)/../shared/build/.../Shared.framework` to Frameworks. The KMP template wizard
/// or `kdoctor` scaffolds this. The Swift code below assumes the generated `Shared` module.
@MainActor
final class SharedContainer: ObservableObject {
    let shared = AtlanShared(databaseDriverFactory: DatabaseDriverFactory())

    func todayDashboard() async -> DashboardState? {
        try? await shared.getTodayDashboard.invoke(
            dateLabel: "Tuesday · May 26",
            offlineStatus: .offlineUsable
        )
    }

    func todaySession() async -> SessionDetailState? {
        try? await shared.getTodaySession.invoke(offlineStatus: .offlineUsable)
    }

    func trainingWeek() async -> TrainingWeek? {
        try? await shared.getTrainingPlan.invoke()
    }

    func whyConcept(key: String, language: Language) async -> WhyModalState? {
        try? await shared.getWhyConcept.invoke(conceptKey: key, language: language)
    }

    func proposeSwap(sessionId: String) async -> SessionSwapperState? {
        try? await shared.proposeSessionSwap.invoke(originalSessionId: sessionId)
    }

    func acceptSwap(proposalId: String, originalSessionId: String) async {
        try? await shared.acceptSessionSwap.accept(
            proposalId: proposalId,
            originalSessionId: originalSessionId
        )
    }

    func skipToday(sessionId: String) async {
        try? await shared.acceptSessionSwap.skipToday(sessionId: sessionId)
    }

    func completeSet(sessionId: String, setId: String) async {
        _ = try? await shared.completeWorkoutSet.invoke(sessionId: sessionId, setId: setId)
    }

    func startTimer(for session: TrainingSession, restSeconds: Int = 30) -> WorkoutTimerState {
        shared.startWorkoutTimer.invoke(
            session: session,
            startIndex: 1,
            setDurationMs: 105_000,
            restDurationMs: Int64(restSeconds) * 1000,
            offlineStatus: .offlineUsable
        )
    }

    // MARK: Active-session persistence (resume / recovery)

    func loadSessionProgress() async -> SessionProgress? {
        try? await shared.loadSessionProgress.invoke()
    }

    func saveSessionProgress(_ timer: WorkoutTimerState, restSeconds: Int) async {
        try? await shared.saveSessionProgress.invoke(timer: timer, restSeconds: Int32(restSeconds), savedAtIso: "")
    }

    func clearSessionProgress(_ sessionId: String) async {
        try? await shared.clearSessionProgress.invoke(sessionId: sessionId)
    }

    func resumeTimer(for session: TrainingSession, progress: SessionProgress) -> WorkoutTimerState {
        shared.resumeWorkoutTimer.invoke(session: session, progress: progress, setDurationMs: 105_000)
    }

    // MARK: Workout history

    func recordCompletedSession(_ timer: WorkoutTimerState, title: String, perceivedEffort: String?, finishedAtIso: String) async {
        try? await shared.recordCompletedSession.invoke(
            timer: timer, title: title, perceivedEffort: perceivedEffort, finishedAtIso: finishedAtIso
        )
    }

    func workoutHistory() async -> [CompletedSession] {
        (try? await shared.getWorkoutHistory.invoke()) ?? []
    }
}
