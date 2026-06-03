import SwiftUI
import Shared

/// Today Dashboard — calm; answers only "what is today?" and "is the week on track?". No streaks,
/// leaderboards, peer ranking, notification badges, red missed rows, or behind-plan messaging.
struct TodayDashboardView: View {
    @EnvironmentObject private var container: SharedContainer
    @EnvironmentObject private var coordinator: AppCoordinator
    @State private var state: DashboardState?
    @State private var resumable: SessionProgress?

    var body: some View {
        ScrollView {
            VStack(spacing: AtlanSpacing.lg) {
                header
                if let p = resumable { resumeBanner(p) }
                if let s = state {
                    if let session = s.todaySession { todayCard(s, session) }
                    weeklyArcCard(s.weeklyArc)
                    Button { coordinator.go(.workoutPlan) } label: {
                        Text(coordinator.language == .es ? "Ver el plan de la semana" : "View this week's plan")
                            .foregroundColor(AtlanColors.tideDeep)
                            .frame(maxWidth: .infinity, minHeight: 44, alignment: .leading)
                            .contentShape(Rectangle())
                    }
                    .buttonStyle(AtlanPressStyle())
                    ForEach(s.metricChips, id: \.key) { chip in
                        AtlanMetricChip(chip: chip) { coordinator.presentWhy($0) }
                    }
                } else {
                    ProgressView().tint(AtlanColors.tide)
                }
            }
            .padding(AtlanSpacing.xl)
        }
        .background(AtlanColors.foamWarm.ignoresSafeArea())
        .navigationBarBackButtonHidden(true)
        .task {
            state = await container.todayDashboard()
            resumable = await container.loadSessionProgress()
        }
    }

    /// Calm offer to pick up an interrupted session — never a "you stopped" nudge. Resume or discard.
    private func resumeBanner(_ p: SessionProgress) -> some View {
        let es = coordinator.language == .es
        return VStack(alignment: .leading, spacing: AtlanSpacing.sm) {
            Text(es ? "Reanudar sesión · Serie \(p.setIndex) de \(p.setCount)"
                    : "Resume session · Set \(p.setIndex) of \(p.setCount)")
                .foregroundColor(AtlanColors.tideDeep)
            HStack(spacing: AtlanSpacing.md) {
                AtlanButton(title: es ? "Reanudar" : "Resume", coral: true) {
                    coordinator.wetResume = true
                    coordinator.go(.wetMode)
                }
                Button {
                    let id = p.sessionId
                    resumable = nil
                    Task { await container.clearSessionProgress(id) }
                } label: {
                    Text(es ? "Descartar" : "Discard")
                        .foregroundColor(AtlanColors.tideDeep)
                        .frame(minHeight: 44).contentShape(Rectangle())
                }
                .buttonStyle(AtlanPressStyle())
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(AtlanSpacing.lg)
        .background(AtlanColors.tidePale)
        .clipShape(RoundedRectangle(cornerRadius: AtlanRadii.lg))
    }

    private var header: some View {
        HStack {
            Text("atlan").font(.title2).foregroundColor(AtlanColors.abyss)
            Spacer()
            Text(state?.dateLabel ?? "").foregroundColor(AtlanColors.tideDeep)
            Button { coordinator.go(.settings) } label: {
                Image(systemName: "gearshape")
                    .foregroundColor(AtlanColors.tideDeep)
                    .frame(width: 44, height: 44)
                    .contentShape(Rectangle())
            }
            .buttonStyle(AtlanPressStyle())
            .accessibilityLabel(coordinator.language == .es ? "Ajustes" : "Settings")
        }
    }

    private func todayCard(_ s: DashboardState, _ session: TrainingSession) -> some View {
        VStack(alignment: .leading, spacing: AtlanSpacing.sm) {
            Text("Today · Pool".uppercased())
                .font(AtlanTypography.label).foregroundColor(AtlanColors.tideSoft)
            Text(session.distanceLabel).font(.system(size: 44)).foregroundColor(AtlanColors.foam)
            HStack {
                Text("Threshold · \(session.durationEstimateLabel)").foregroundColor(AtlanColors.tideSoft)
                if let key = s.todayWhyConceptKey {
                    AtlanInfoButton { coordinator.presentWhy(key) }
                }
            }
            AtlanButton(title: "Start session →", coral: true) { coordinator.go(.sessionDetail) }
                .padding(.top, AtlanSpacing.sm)
            Button {
                coordinator.presentSwapper(session.id)
            } label: {
                Text(coordinator.language == .es ? "Ajustar hoy" : "Adjust today")
                    .foregroundColor(AtlanColors.tideSoft)
                    .frame(maxWidth: .infinity, minHeight: 44)
                    .contentShape(Rectangle())
            }
            .buttonStyle(AtlanPressStyle())
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(AtlanSpacing.xl)
        .background(AtlanColors.abyss)
        .clipShape(RoundedRectangle(cornerRadius: AtlanRadii.phone))
    }

    private func weeklyArcCard(_ arc: WeeklyArcState) -> some View {
        VStack(alignment: .leading, spacing: AtlanSpacing.sm) {
            Text(arc.label.uppercased()).font(AtlanTypography.label).foregroundColor(AtlanColors.tideDeep)
            Text(arc.status).font(.title3).foregroundColor(AtlanColors.abyss)
            WeeklyArcChart(points: arc.points.map { CGFloat(truncating: $0) },
                           markerIndex: Int(arc.currentWeekMarker))
                .frame(height: 72)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(AtlanSpacing.lg)
        .background(AtlanColors.paper)
        .clipShape(RoundedRectangle(cornerRadius: AtlanRadii.sheet))
    }
}

/// Calm line chart placeholder with a single Coral marker for the current week.
private struct WeeklyArcChart: View {
    let points: [CGFloat]
    let markerIndex: Int

    var body: some View {
        GeometryReader { geo in
            let w = geo.size.width, h = geo.size.height
            if points.count > 1 {
                let stepX = w / CGFloat(points.count - 1)
                let pts = points.enumerated().map { CGPoint(x: CGFloat($0.offset) * stepX,
                                                            y: h - ($0.element * h)) }
                Path { p in
                    p.move(to: pts[0])
                    pts.dropFirst().forEach { p.addLine(to: $0) }
                }
                .stroke(AtlanColors.tide, lineWidth: 3)

                let marker = pts.indices.contains(markerIndex) ? pts[markerIndex] : pts[pts.count - 1]
                Circle().fill(AtlanColors.coral).frame(width: 10, height: 10)
                    .position(marker)
            }
        }
    }
}
