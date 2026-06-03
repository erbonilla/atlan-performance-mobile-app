import SwiftUI
import Shared

/// Workout Prep — the final readiness step before the active 4-set timer (inventory §8.4). Confirms
/// the session shape and offline availability, offers a calm warm-up reminder, then starts Wet Mode.
/// Loads from cache; offline-first. Back returns to Session Detail without losing anything.
struct WorkoutPrepView: View {
    @EnvironmentObject private var container: SharedContainer
    @EnvironmentObject private var coordinator: AppCoordinator
    @State private var state: SessionDetailState?

    private struct PrepRow: Identifiable { let id = UUID(); let label: String; let value: String }

    var body: some View {
        let isES = coordinator.language == .es
        ScrollView {
            VStack(alignment: .leading, spacing: AtlanSpacing.md) {
                if let s = state {
                    let session = s.session
                    let mainSets = session.sets.filter { $0.targetPaceLabel != nil }
                    let pace = mainSets.first?.targetPaceLabel ?? "—"
                    let perSet = mainSets.first?.distanceLabel ?? session.distanceLabel

                    if session.offlineAvailable {
                        AtlanPill(text: isES ? "Sin conexión · Listo" : "Offline · Ready")
                    }
                    Text((isES ? "Listo para empezar" : "Ready to begin").uppercased())
                        .font(AtlanTypography.label).foregroundColor(AtlanColors.tideDeep)
                    Text(session.title).font(AtlanTypography.display).foregroundColor(AtlanColors.abyss)
                        .fixedSize(horizontal: false, vertical: true)

                    VStack(spacing: 0) {
                        prepRow(isES ? "Tipo" : "Type", "Threshold")
                        Divider()
                        prepRow(isES ? "Series" : "Sets", "\(mainSets.count) × \(perSet)")
                        Divider()
                        prepRow(isES ? "Ritmo objetivo" : "Target pace", pace)
                        Divider()
                        prepRow(isES ? "Tiempo estimado" : "Estimated time", session.durationEstimateLabel)
                    }
                    .padding(AtlanSpacing.lg)
                    .background(AtlanColors.paper)
                    .clipShape(RoundedRectangle(cornerRadius: AtlanRadii.lg))

                    Text(isES
                         ? "Tómate un momento para calentar. El cronómetro empieza cuando estés listo."
                         : "Take a moment to warm up. The timer starts when you're ready.")
                        .foregroundColor(AtlanColors.tideDeep)
                        .fixedSize(horizontal: false, vertical: true)
                        .padding(.top, AtlanSpacing.sm)

                    Spacer(minLength: AtlanSpacing.xxl)
                    AtlanButton(title: isES ? "Empezar sesión" : "Begin session", coral: true) {
                        coordinator.go(.wetMode)
                    }
                } else {
                    ProgressView().tint(AtlanColors.tide)
                }
            }
            .padding(AtlanSpacing.xl)
        }
        .background(AtlanColors.foamWarm.ignoresSafeArea())
        .task { state = await container.todaySession() }
    }

    private func prepRow(_ label: String, _ value: String) -> some View {
        HStack {
            Text(label).foregroundColor(AtlanColors.abyss)
            Spacer()
            Text(value).foregroundColor(AtlanColors.tideDeep)
        }
        .frame(minHeight: 44)
    }
}
