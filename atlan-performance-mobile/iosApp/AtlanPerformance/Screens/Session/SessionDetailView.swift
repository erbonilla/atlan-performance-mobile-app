import SwiftUI
import Shared

/// Session Detail — shows session structure with minimal load. Tapping the `i` beside threshold
/// opens the Why Modal; Start session opens Wet Mode for this initial build. Works offline from cache.
struct SessionDetailView: View {
    @EnvironmentObject private var container: SharedContainer
    @EnvironmentObject private var coordinator: AppCoordinator
    @State private var state: SessionDetailState?
    @State private var loadFailed = false

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: AtlanSpacing.md) {
                if loadFailed && state == nil {
                    AtlanErrorView(
                        title: coordinator.language == .es ? "No pudimos cargar la sesión" : "We couldn't load the session",
                        message: coordinator.language == .es
                            ? "Tus datos están a salvo. Inténtalo de nuevo."
                            : "Your data is safe. Try again.",
                        retryTitle: coordinator.language == .es ? "Reintentar" : "Retry",
                        onRetry: { Task { await load() } }
                    )
                } else if let s = state {
                    let session = s.session
                    if session.offlineAvailable { AtlanPill(text: "Offline · Cached") }
                    Text("Today · Tuesday, May 26".uppercased())
                        .font(AtlanTypography.label).foregroundColor(AtlanColors.tideDeep)
                    Text(session.title).font(AtlanTypography.display).foregroundColor(AtlanColors.abyss)
                    Text("\(session.distanceLabel) · \(session.durationEstimateLabel)")
                        .foregroundColor(AtlanColors.tideDeep)

                    structureCard(s)

                    AtlanButton(title: coordinator.language == .es ? "Empezar sesión" : "Start session",
                                coral: true) {
                        coordinator.go(.workoutPrep)
                    }
                    .padding(.top, AtlanSpacing.lg)
                } else {
                    ProgressView().tint(AtlanColors.tide)
                }
            }
            .padding(AtlanSpacing.xl)
        }
        .background(AtlanColors.foamWarm.ignoresSafeArea())
        .task { await load() }
    }

    private func load() async {
        loadFailed = false
        let result = await container.todaySession()
        state = result
        loadFailed = result == nil
    }

    private func structureCard(_ s: SessionDetailState) -> some View {
        VStack(alignment: .leading, spacing: AtlanSpacing.md) {
            ForEach(s.session.sets, id: \.id) { set in
                HStack {
                    VStack(alignment: .leading, spacing: 2) {
                        Text(set.label).foregroundColor(AtlanColors.abyss)
                        if let pace = set.targetPaceLabel {
                            Text("Target pace \(pace)").font(.footnote).foregroundColor(AtlanColors.tideDeep)
                        }
                    }
                    Spacer()
                    if set.targetPaceLabel != nil, let key = s.whyConceptKey {
                        AtlanInfoButton { coordinator.presentWhy(key) }
                    }
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(AtlanSpacing.lg)
        .background(AtlanColors.paper)
        .clipShape(RoundedRectangle(cornerRadius: AtlanRadii.sheet))
    }
}
