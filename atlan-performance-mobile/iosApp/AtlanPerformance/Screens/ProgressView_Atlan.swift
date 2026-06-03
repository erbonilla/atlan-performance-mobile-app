import SwiftUI
import Shared

/// Progress Overview — calm, qualitative training summary from local history. No streaks, no goal
/// deficit, no red; "adjusted" (ended-early) sessions are valid, not failures. Named `_Atlan` to
/// avoid clashing with SwiftUI's built-in `ProgressView`. Pushed from Settings.
struct ProgressView_Atlan: View {
    @EnvironmentObject private var container: SharedContainer
    @EnvironmentObject private var coordinator: AppCoordinator
    @State private var overview: ProgressOverview?

    var body: some View {
        let es = coordinator.language == .es
        ScrollView {
            VStack(alignment: .leading, spacing: AtlanSpacing.md) {
                Text(es ? "Progreso" : "Progress")
                    .font(AtlanTypography.display).foregroundColor(AtlanColors.abyss)

                if let o = overview {
                    if o.hasAny {
                        card {
                            Text(es ? "Constancia" : "Consistency")
                                .font(AtlanTypography.label).foregroundColor(AtlanColors.tideDeep)
                            Text(es ? "\(o.sessionCount) sesiones · \(o.setsCompleted) series"
                                    : "\(o.sessionCount) sessions · \(o.setsCompleted) sets")
                                .font(.title2).foregroundColor(AtlanColors.abyss)
                            Text(es ? "\(o.fullSessions) completas · \(o.adjustedSessions) ajustadas"
                                    : "\(o.fullSessions) complete · \(o.adjustedSessions) adjusted")
                                .foregroundColor(AtlanColors.tideDeep)
                        }
                        if o.effortLogged > 0 {
                            card {
                                Text(es ? "Esfuerzo percibido" : "Perceived effort")
                                    .font(AtlanTypography.label).foregroundColor(AtlanColors.tideDeep)
                                Text(effortLine(o, es))
                                    .foregroundColor(AtlanColors.abyss)
                            }
                        }
                        Text(es ? "El progreso es un patrón, no un número que defender."
                                : "Progress is a pattern, not a number to defend.")
                            .font(.footnote).foregroundColor(AtlanColors.tideDeep)
                    } else {
                        card {
                            Text(es ? "Aún no hay progreso" : "No progress yet")
                                .font(AtlanTypography.label).foregroundColor(AtlanColors.tideDeep)
                            Text(es ? "Cuando completes sesiones, tu patrón aparecerá aquí."
                                    : "When you complete sessions, your pattern will appear here.")
                                .foregroundColor(AtlanColors.abyss)
                                .fixedSize(horizontal: false, vertical: true)
                        }
                    }
                } else {
                    SwiftUI.ProgressView().tint(AtlanColors.tide)
                }
            }
            .padding(AtlanSpacing.xl)
        }
        .background(AtlanColors.foamWarm.ignoresSafeArea())
        .task { overview = await container.progressOverview() }
    }

    private func effortLine(_ o: ProgressOverview, _ es: Bool) -> String {
        let easy = (es ? "Fácil" : "Easy") + " \(o.effortEasy)"
        let moderate = (es ? "Moderado" : "Moderate") + " \(o.effortModerate)"
        let hard = (es ? "Duro" : "Hard") + " \(o.effortHard)"
        return [easy, moderate, hard].joined(separator: " · ")
    }

    private func card<Content: View>(@ViewBuilder _ content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: AtlanSpacing.xs) { content() }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(AtlanSpacing.lg)
            .background(AtlanColors.paper)
            .clipShape(RoundedRectangle(cornerRadius: AtlanRadii.lg))
    }
}
