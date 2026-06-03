import SwiftUI
import Shared

/// Workout History — finished sessions (newest first) from the local SQLDelight store. Calm record:
/// set counts, elapsed, optional effort. Partial sessions are valid, never failures. Pushed from
/// Settings; uses the native NavigationStack back.
struct HistoryView: View {
    @EnvironmentObject private var container: SharedContainer
    @EnvironmentObject private var coordinator: AppCoordinator
    @State private var history: [CompletedSession]?

    var body: some View {
        let es = coordinator.language == .es
        ScrollView {
            VStack(alignment: .leading, spacing: AtlanSpacing.md) {
                Text(es ? "Historial" : "History")
                    .font(AtlanTypography.display).foregroundColor(AtlanColors.abyss)

                if let items = history {
                    if items.isEmpty {
                        emptyCard(es)
                    } else {
                        ForEach(items, id: \.id) { row($0, es: es) }
                    }
                } else {
                    ProgressView().tint(AtlanColors.tide)
                }
            }
            .padding(AtlanSpacing.xl)
        }
        .background(AtlanColors.foamWarm.ignoresSafeArea())
        .task { history = await container.workoutHistory() }
    }

    private func row(_ item: CompletedSession, es: Bool) -> some View {
        let sets = es ? "\(item.completedSetCount) de \(item.totalSetCount) series"
                      : "\(item.completedSetCount) of \(item.totalSetCount) sets"
        return VStack(alignment: .leading, spacing: AtlanSpacing.xs) {
            HStack {
                Text(item.title).font(AtlanTypography.label).foregroundColor(AtlanColors.abyss)
                Spacer()
                Text(String(item.finishedAtIso.prefix(10))).foregroundColor(AtlanColors.tideDeep)
            }
            Text("\(sets) · \(item.totalElapsedLabel)").foregroundColor(AtlanColors.tideDeep)
            HStack(spacing: AtlanSpacing.sm) {
                if !item.fullyCompleted { AtlanPill(text: es ? "Parcial" : "Partial") }
                if let effort = item.perceivedEffort { AtlanPill(text: effortLabel(effort, es)) }
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(AtlanSpacing.lg)
        .background(AtlanColors.paper)
        .clipShape(RoundedRectangle(cornerRadius: AtlanRadii.lg))
    }

    private func emptyCard(_ es: Bool) -> some View {
        VStack(alignment: .leading, spacing: AtlanSpacing.xs) {
            Text(es ? "Aún no hay sesiones" : "No sessions yet")
                .font(AtlanTypography.label).foregroundColor(AtlanColors.tideDeep)
            Text(es ? "Cuando termines una sesión, aparecerá aquí."
                    : "When you finish a session, it'll appear here.")
                .foregroundColor(AtlanColors.abyss)
                .fixedSize(horizontal: false, vertical: true)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(AtlanSpacing.xl)
        .background(AtlanColors.paper)
        .clipShape(RoundedRectangle(cornerRadius: AtlanRadii.lg))
    }

    private func effortLabel(_ key: String, _ es: Bool) -> String {
        switch key {
        case "easy": return es ? "Fácil" : "Easy"
        case "moderate": return es ? "Moderado" : "Moderate"
        case "hard": return es ? "Duro" : "Hard"
        default: return key
        }
    }
}
