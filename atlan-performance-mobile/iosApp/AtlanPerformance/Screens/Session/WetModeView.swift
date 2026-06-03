import SwiftUI
import Shared

/// Wet Mode — full-screen, offline, wet-hands 4-set threshold timer (atlan-4-set-timer spec).
/// AbyssDeep bg, Foam text, CoralBright only for productive actions. Very large type, no precision
/// controls.
///
/// Timing is drift-free: the shared `WorkoutTimerState` derives remaining time from a monotonic
/// `nowMs` we supply each tick, never from decrementing UI counters. Flow: Active → (Pause) →
/// Overtime → complete → Rest → Active … → Session summary. Exiting an active session always asks
/// to confirm (progress is never discarded silently).
///
/// Gestures: swipe right = complete / skip rest, swipe left = pause/resume, long swipe down = exit
/// (confirmation). Accessible Complete/Pause/Exit actions are always present.
///
/// TODO(production): tune swipe thresholds for waterproof-pouch use; persist timer state on
/// background + restore on resume; queue completion for real sync. Thresholds below are large.
struct WetModeView: View {
    @EnvironmentObject private var container: SharedContainer
    @EnvironmentObject private var coordinator: AppCoordinator

    @State private var timer: WorkoutTimerState?
    @State private var nowMs: Int64 = 0
    @State private var showEarlyConfirm = false
    @State private var showExitConfirm = false
    @State private var showTutorial = false
    // Offline-resilience surfacing on the summary. Stub until a real sync queue exists.
    @State private var syncState: OfflineStatus = .syncPending
    @State private var syncing = false
    // Optional post-session reflection (perceived effort). Local-only; TODO(persist) with results.
    @State private var effort: Int? = nil

    private let ticker = Timer.publish(every: 1, on: .main, in: .common).autoconnect()
    private let swipeThreshold: CGFloat = 120

    /// Selected language — copy resolves through the shared bilingual layer so ES is first-class here too.
    private var lang: Language { coordinator.language }

    private func monoNow() -> Int64 { Int64(DispatchTime.now().uptimeNanoseconds / 1_000_000) }

    var body: some View {
        ZStack {
            AtlanColors.abyssDeep.ignoresSafeArea()
            if let t = timer {
                switch t.phase {
                case SetTimerPhase.completedSession: summaryBody(t)
                case SetTimerPhase.rest: restBody(t)
                default: activeBody(t)
                }
            } else {
                ProgressView().tint(AtlanColors.tideSoft)
            }

            if showTutorial { tutorialOverlay }
        }
        .onAppear {
            if !coordinator.wetModeTutorialSeen { showTutorial = true }
            // Keep the screen on during an active workout if the user prefers it.
            UIApplication.shared.isIdleTimerDisabled = coordinator.keepScreenAwake
        }
        .onDisappear { UIApplication.shared.isIdleTimerDisabled = false }
        .navigationBarBackButtonHidden(true)
        .contentShape(Rectangle())
        // simultaneousGesture (not gesture) so the big button taps still fire — a plain tap never
        // reaches the drag's minimumDistance, so only real swipes trigger it.
        .simultaneousGesture(dragGesture)
        .accessibilityAction(named: localized(.wetModeActionComplete, lang)) { primaryForwardAction() }
        .accessibilityAction(named: localized(.wetModeActionPause, lang)) { togglePause() }
        .accessibilityAction(named: localized(.wetModeActionExit, lang)) { requestExit() }
        .onReceive(ticker) { _ in tick() }
        .confirmationDialog(localized(.wetModeEarlyTitle, lang), isPresented: $showEarlyConfirm,
                            titleVisibility: .visible) {
            Button(localized(.wetModeEarlyConfirm, lang)) { performComplete() }
            Button(localized(.wetModeKeepGoing, lang), role: .cancel) {}
        } message: {
            if let t = timer {
                Text(localized(.wetModeEarlyMessage, lang, t.setLabel, t.timerLabel(nowMs: nowMs)))
            }
        }
        .confirmationDialog(localized(.wetModeExitTitle, lang), isPresented: $showExitConfirm,
                            titleVisibility: .visible) {
            Button(localized(.wetModeEndSession, lang), role: .destructive) { endSession() }
            Button(localized(.wetModeKeepGoing, lang), role: .cancel) {}
        } message: {
            Text(localized(.wetModeExitMessage, lang))
        }
        .task {
            if timer == nil, let session = await container.todaySession()?.session {
                nowMs = monoNow()
                timer = container.startTimer(for: session, restSeconds: coordinator.restSeconds).started(nowMs: nowMs)
            }
        }
    }

    // MARK: Active set

    private func activeBody(_ t: WorkoutTimerState) -> some View {
        let overtime = t.phase == SetTimerPhase.overtime
        return VStack(spacing: AtlanSpacing.sm) {
            Text(t.offlineStatus.pillLabel(lang)).foregroundColor(AtlanColors.tideSoft)
            Text(t.setLabel).font(.title3).foregroundColor(AtlanColors.foam)
            Text(t.mainMetric).font(.system(size: 56, weight: .bold)).foregroundColor(AtlanColors.foam)
                .padding(.top, AtlanSpacing.md)
            Text(t.intensityLabel).font(.title2).foregroundColor(AtlanColors.tideSoft)

            Text(t.timerLabel(nowMs: nowMs))
                .font(AtlanTypography.wetMode)
                .foregroundColor(overtime ? AtlanColors.coralBright : AtlanColors.foam)
                .accessibilityLabel(timerAccessibilityLabel(t))
            if let pace = t.targetPaceLabel {
                Text(overtime ? localized(.wetModeOverTargetPace, lang) : localized(.wetModeTargetPace, lang, pace))
                    .foregroundColor(overtime ? AtlanColors.coralBright : AtlanColors.tideSoft)
            }
            if t.isPaused {
                Text(localized(.wetModePaused, lang)).font(.title3).foregroundColor(AtlanColors.coralBright)
                Button { requestExit() } label: {
                    Text(localized(.wetModeEndSession, lang)).foregroundColor(AtlanColors.tideSoft)
                        .frame(minWidth: 120, minHeight: 44).contentShape(Rectangle())
                }
                .buttonStyle(AtlanPressStyle())
            } else if overtime {
                Text(localized(.wetModeOverTarget, lang)).foregroundColor(AtlanColors.coralBright)
            }

            Spacer()
            twoZone(
                leftTitle: localized(t.isPaused ? .wetModeResume : .wetModePause, lang),
                leftLabel: localized(t.isPaused ? .wetModeLabelResume : .wetModeLabelPause, lang),
                onLeft: { togglePause() },
                rightTitle: localized(.wetModeComplete, lang),
                rightLabel: localized(.wetModeLabelComplete, lang),
                onRight: { attemptComplete() }
            )
        }
        .padding(.top, AtlanSpacing.xxxl)
    }

    // MARK: Rest between sets

    private func restBody(_ t: WorkoutTimerState) -> some View {
        VStack(spacing: AtlanSpacing.sm) {
            Text(t.offlineStatus.pillLabel(lang)).foregroundColor(AtlanColors.tideSoft)
            Text(localized(.wetModeSetComplete, lang, "\(t.justCompletedSetNumber)"))
                .font(.title3).foregroundColor(AtlanColors.foam)
            Text(localized(.wetModeRest, lang)).font(.title2).foregroundColor(AtlanColors.tideSoft).padding(.top, AtlanSpacing.md)
            Text(t.timerLabel(nowMs: nowMs)).font(AtlanTypography.wetMode).foregroundColor(AtlanColors.foam)
            Text("\(localized(.wetModeNext, lang)) · \(t.setLabel) · \(t.mainMetric)").foregroundColor(AtlanColors.tideSoft)
            Spacer()
            twoZone(
                leftTitle: localized(.wetModeEnd, lang),
                leftLabel: localized(.wetModeEndSession, lang),
                onLeft: { requestExit() },
                rightTitle: localized(.wetModeSkipRest, lang),
                rightLabel: localized(.wetModeLabelSkipRest, lang),
                onRight: { skipRest() }
            )
        }
        .padding(.top, AtlanSpacing.xxxl)
    }

    // MARK: Session summary

    private func summaryBody(_ t: WorkoutTimerState) -> some View {
        let full = t.completedCount >= t.setCount
        return VStack(spacing: AtlanSpacing.md) {
            Spacer()
            Text(localized(full ? .wetModeSummaryComplete : .wetModeSummaryEnded, lang))
                .font(AtlanTypography.display).foregroundColor(AtlanColors.foam)
            Text(localized(.wetModeSummarySets, lang, "\(t.completedCount)", "\(t.setCount)") + " · \(t.totalElapsedLabel())")
                .font(.title3).foregroundColor(AtlanColors.tideSoft)

            VStack(spacing: AtlanSpacing.xs) {
                ForEach(0..<Int(t.setCount), id: \.self) { i in
                    HStack {
                        Text(localized(.wetModeSet, lang, "\(i + 1)")).foregroundColor(AtlanColors.foam)
                        Spacer()
                        Text(setDetail(t, i)).foregroundColor(AtlanColors.tideSoft)
                        Image(systemName: i < Int(t.completedCount) ? "checkmark.circle.fill" : "circle")
                            .foregroundColor(i < Int(t.completedCount) ? AtlanColors.tide : AtlanColors.tideSoft)
                    }
                    .padding(.vertical, AtlanSpacing.xs)
                }
            }
            .padding(AtlanSpacing.lg)
            .background(AtlanColors.abyss)
            .clipShape(RoundedRectangle(cornerRadius: AtlanRadii.lg))
            .padding(.horizontal, AtlanSpacing.xl)

            effortRow()
            syncBlock()
            Spacer()
            AtlanButton(title: localized(.wetModeDone, lang), coral: true) { coordinator.pop() }
                .padding(.horizontal, AtlanSpacing.xl)
                .padding(.bottom, AtlanSpacing.xxl)
        }
    }

    /// Calm offline/sync status with a Retry affordance (never red; data is always safe locally).
    /// Optional perceived-effort reflection. No keyboard; calm, never scored or judged. Selection
    /// shows a ✓ + Tide fill (color is never the only cue).
    private func effortRow() -> some View {
        let es = coordinator.language == .es
        let labels = es ? ["Fácil", "Moderado", "Duro"] : ["Easy", "Moderate", "Hard"]
        return VStack(spacing: AtlanSpacing.sm) {
            Text(es ? "¿Cómo se sintió?" : "How did that feel?")
                .font(.footnote).foregroundColor(AtlanColors.tideSoft)
            HStack(spacing: AtlanSpacing.sm) {
                ForEach(Array(labels.enumerated()), id: \.offset) { i, label in
                    let selected = effort == i
                    Button { effort = selected ? nil : i } label: {
                        Text(selected ? "✓ \(label)" : label)
                            .foregroundColor(selected ? AtlanColors.foam : AtlanColors.tideSoft)
                            .padding(.horizontal, AtlanSpacing.lg)
                            .frame(minHeight: 40)
                            .background(selected ? AtlanColors.tide : AtlanColors.abyss)
                            .clipShape(Capsule())
                            .contentShape(Capsule())
                    }
                    .buttonStyle(AtlanPressStyle())
                    .accessibilityAddTraits(selected ? [.isButton, .isSelected] : .isButton)
                }
            }
        }
        .padding(.horizontal, AtlanSpacing.xl)
    }

    private func syncBlock() -> some View {
        VStack(spacing: AtlanSpacing.sm) {
            if syncing {
                HStack(spacing: AtlanSpacing.sm) {
                    ProgressView().tint(AtlanColors.tideSoft)
                    Text(localized(.wetModeSyncing, lang)).foregroundColor(AtlanColors.tideSoft)
                }
            } else {
                let failed = syncState == .syncFailedSavedLocally
                AtlanPill(text: localized(failed ? .wetModeOfflineSavedLocally : .wetModeOfflineSyncPending, lang))
                Text(localized(failed ? .wetModeSyncFailed : .wetModeSyncSavedOffline, lang))
                    .font(.footnote).multilineTextAlignment(.center)
                    .foregroundColor(AtlanColors.tideSoft)
                    .fixedSize(horizontal: false, vertical: true)
                Button { retrySync() } label: {
                    Text(localized(.wetModeRetrySync, lang)).foregroundColor(AtlanColors.tideSoft).bold()
                        .frame(minHeight: 44).contentShape(Rectangle())
                }
                .buttonStyle(AtlanPressStyle())
            }
        }
        .padding(.horizontal, AtlanSpacing.xl)
    }

    private func retrySync() {
        // TODO(sync): drive a real sync-queue drain. Offline in this build → stays safely local.
        syncing = true
        Task {
            try? await Task.sleep(nanoseconds: 1_200_000_000)
            syncing = false
            syncState = .syncFailedSavedLocally
        }
    }

    private func setDetail(_ t: WorkoutTimerState, _ i: Int) -> String {
        let set = t.sets[i]
        if let pace = set.targetPaceLabel { return "\(set.mainMetric) · \(pace)" }
        return set.mainMetric
    }

    // MARK: Reusable two-zone action grid

    private func twoZone(leftTitle: String, leftLabel: String, onLeft: @escaping () -> Void,
                         rightTitle: String, rightLabel: String, onRight: @escaping () -> Void) -> some View {
        HStack(spacing: 0) {
            WetModeActionZone(title: leftTitle, hint: localized(.wetModeSwipeLeftHint, lang), accessibleLabel: leftLabel,
                              productive: false, action: onLeft)
            WetModeActionZone(title: rightTitle, hint: localized(.wetModeSwipeRightHint, lang), accessibleLabel: rightLabel,
                              productive: true, action: onRight)
        }
        .frame(height: 220)
    }

    // MARK: Actions

    private func tick() {
        nowMs = monoNow()
        if let t = timer, t.isRunningOrOvertime || t.isResting { timer = t.ticked(nowMs: nowMs) }
    }

    /// Swipe-right / accessible "Complete" maps to the phase's forward action.
    private func primaryForwardAction() {
        guard let t = timer else { return }
        if t.isResting { skipRest() } else { attemptComplete() }
    }

    private func togglePause() {
        guard let t = timer else { return }
        nowMs = monoNow()
        if t.isPaused {
            timer = t.resumed(nowMs: nowMs)
            impact(.light)
        } else if t.isRunningOrOvertime {
            timer = t.paused(nowMs: nowMs)
            impact(.light)
            announce(localized(.wetModeAnnouncePaused, lang))
        }
    }

    private func attemptComplete() {
        guard let t = timer, !t.isComplete, !t.isResting else { return }
        nowMs = monoNow()
        if t.requiresEarlyCompleteConfirm(nowMs: nowMs, thresholdMs: 10_000) {
            showEarlyConfirm = true
        } else {
            performComplete()
        }
    }

    private func performComplete() {
        guard let t = timer else { return }
        // Local-first write of the completed set (offline-safe).
        Task { await container.completeSet(sessionId: t.sessionId, setId: t.currentSetId) }
        if coordinator.hapticsEnabled { UINotificationFeedbackGenerator().notificationOccurred(.success) }
        nowMs = monoNow()
        let advanced = t.completedSet(nowMs: nowMs)
        timer = advanced
        announce(advanced.isComplete
                 ? localized(.wetModeSummaryComplete, lang)
                 : localized(.wetModeAnnounceSetCompleteRest, lang, "\(t.justCompletedSetNumber)"))
    }

    private func skipRest() {
        guard let t = timer, t.isResting else { return }
        nowMs = monoNow()
        timer = t.skipRest(nowMs: nowMs)
        UIImpactFeedbackGenerator(style: .light).impactOccurred()
        announce(localized(.wetModeAnnounceSetStarted, lang, timer?.setLabel ?? localized(.wetModeNext, lang)))
    }

    private func requestExit() {
        guard let t = timer else { coordinator.pop(); return }
        if t.isComplete { coordinator.pop() } else { showExitConfirm = true }
    }

    private func endSession() {
        guard let t = timer else { coordinator.pop(); return }
        nowMs = monoNow()
        timer = t.endedSession(nowMs: nowMs)
        announce(localized(.wetModeSummaryEnded, lang))
    }

    private func impact(_ style: UIImpactFeedbackGenerator.FeedbackStyle) {
        if coordinator.hapticsEnabled { UIImpactFeedbackGenerator(style: style).impactOccurred() }
    }

    private func announce(_ message: String) {
        UIAccessibility.post(notification: .announcement, argument: message)
    }

    private func timerAccessibilityLabel(_ t: WorkoutTimerState) -> String {
        let remaining = t.remainingMs(nowMs: nowMs)
        if remaining < 0 { return localized(.wetModeOverTarget, lang) }
        let secs = Int((remaining + 999) / 1000)
        return "\(secs / 60) minutes \(secs % 60) seconds remaining"
    }

    // MARK: Gesture coach mark (one-time)

    private var tutorialOverlay: some View {
        ZStack {
            AtlanColors.abyssDeep.opacity(0.92).ignoresSafeArea()
            VStack(alignment: .leading, spacing: AtlanSpacing.lg) {
                Text(localized(.wetModeTutorialTitle, lang))
                    .font(AtlanTypography.title).foregroundColor(AtlanColors.foam)
                tutorialRow("arrow.right", localized(.wetModeTutorialComplete, lang))
                tutorialRow("arrow.left", localized(.wetModeTutorialPause, lang))
                tutorialRow("arrow.down", localized(.wetModeTutorialExit, lang))
                AtlanButton(title: localized(.wetModeTutorialGotIt, lang), coral: true) { dismissTutorial() }
                    .padding(.top, AtlanSpacing.sm)
            }
            .padding(AtlanSpacing.xl)
        }
        .contentShape(Rectangle())
        .accessibilityElement(children: .contain)
    }

    private func tutorialRow(_ icon: String, _ text: String) -> some View {
        HStack(spacing: AtlanSpacing.md) {
            Image(systemName: icon).foregroundColor(AtlanColors.coralBright).frame(width: 28)
            Text(text).foregroundColor(AtlanColors.foam)
                .fixedSize(horizontal: false, vertical: true)
        }
    }

    private func dismissTutorial() {
        showTutorial = false
        coordinator.wetModeTutorialSeen = true
    }

    private var dragGesture: some Gesture {
        DragGesture(minimumDistance: 20)
            .onEnded { value in
                if showTutorial { return }     // coach mark intercepts gestures
                let dx = value.translation.width
                let dy = value.translation.height
                if dy > swipeThreshold * 1.5 {
                    requestExit()                  // long swipe down → exit confirmation
                } else if dx > swipeThreshold {
                    primaryForwardAction()         // swipe right → complete / skip rest
                } else if dx < -swipeThreshold {
                    togglePause()                  // swipe left → pause/resume
                }
            }
    }
}
