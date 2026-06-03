import SwiftUI
import Shared

/// Workout Plan List — the current week's sessions. Completed and upcoming sessions are equal, calm
/// states; only today's session is actionable (opens Session Detail). No streak, no "missed", no
/// red. Falls back to a calm empty state if the week has no sessions. Pushed from the dashboard.
struct WorkoutPlanListView: View {
    @EnvironmentObject private var container: SharedContainer
    @EnvironmentObject private var coordinator: AppCoordinator
    @State private var week: TrainingWeek?
    @State private var todayId: String?

    var body: some View {
        let es = coordinator.language == .es
        ScrollView {
            VStack(alignment: .leading, spacing: AtlanSpacing.md) {
                Text(es ? "Tu plan" : "Your plan")
                    .font(AtlanTypography.display).foregroundColor(AtlanColors.abyss)

                if let w = week {
                    Text(es
                         ? "Semana \(w.index) de \(w.totalWeeks) · En curso"
                         : "Week \(w.index) of \(w.totalWeeks) · On track")
                        .foregroundColor(AtlanColors.tideDeep)

                    let sessions = w.sessions
                    if sessions.isEmpty {
                        emptyCard(es)
                    } else {
                        ForEach(sessions, id: \.id) { session in
                            row(session, es: es)
                        }
                    }
                } else {
                    ProgressView().tint(AtlanColors.tide)
                }
            }
            .padding(AtlanSpacing.xl)
        }
        .background(AtlanColors.foamWarm.ignoresSafeArea())
        .task {
            week = await container.trainingWeek()
            todayId = await container.todaySession()?.session.id
        }
    }

    @ViewBuilder
    private func row(_ session: TrainingSession, es: Bool) -> some View {
        let isToday = session.id == todayId
        if isToday {
            Button { coordinator.go(.sessionDetail) } label: { rowContent(session, isToday: true, es: es) }
                .buttonStyle(AtlanPressStyle())
        } else {
            rowContent(session, isToday: false, es: es)
        }
    }

    private func rowContent(_ session: TrainingSession, isToday: Bool, es: Bool) -> some View {
        let status: String = {
            if session.status == .completed { return es ? "Completada" : "Completed" }
            if isToday { return es ? "Hoy" : "Today" }
            return es ? "Próxima" : "Upcoming"
        }()
        return HStack(alignment: .center, spacing: AtlanSpacing.md) {
            VStack(alignment: .leading, spacing: 2) {
                Text(session.title).font(AtlanTypography.label).foregroundColor(AtlanColors.abyss)
                Text("\(session.distanceLabel) · \(session.durationEstimateLabel)")
                    .foregroundColor(AtlanColors.tideDeep)
                if session.offlineAvailable {
                    Text(es ? "Sin conexión · Listo" : "Offline · Ready")
                        .font(AtlanTypography.label).foregroundColor(AtlanColors.tideDeep)
                }
            }
            Spacer()
            AtlanPill(text: status)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .frame(minHeight: 64)
        .padding(AtlanSpacing.lg)
        .background(AtlanColors.paper)
        .clipShape(RoundedRectangle(cornerRadius: AtlanRadii.lg))
    }

    private func emptyCard(_ es: Bool) -> some View {
        VStack(alignment: .leading, spacing: AtlanSpacing.xs) {
            Text(es ? "Nada programado esta semana" : "Nothing scheduled this week")
                .font(AtlanTypography.label).foregroundColor(AtlanColors.tideDeep)
            Text(es
                 ? "Cuando haya sesiones, aparecerán aquí. Disfruta el descanso."
                 : "When sessions are scheduled, they'll appear here. Enjoy the rest.")
                .foregroundColor(AtlanColors.abyss)
                .fixedSize(horizontal: false, vertical: true)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(AtlanSpacing.xl)
        .background(AtlanColors.paper)
        .clipShape(RoundedRectangle(cornerRadius: AtlanRadii.lg))
    }
}
