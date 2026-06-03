import SwiftUI
import Shared

/// App entry. Launches to Language Selection (chosen before account creation). The shared core is
/// created once and injected; its fake repositories are seeded on construction (offline-first source
/// of truth). Why Modal and Session Swapper are presented as native sheets over the stack.
@main
struct AtlanPerformanceApp: App {
    @StateObject private var container = SharedContainer()
    @StateObject private var coordinator = AppCoordinator()
    @State private var splashDone = false

    var body: some Scene {
        WindowGroup {
            ZStack {
            NavigationStack(path: $coordinator.path) {
                LanguageSelectionView()
                    .navigationDestination(for: AppRoute.self) { route in
                        destination(for: route)
                            .navigationBarBackButtonHidden(false)
                    }
            }
            .environmentObject(container)
            .environmentObject(coordinator)
            .tint(AtlanColors.tide)
            // Why Modal — native sheet.
            .sheet(item: Binding(
                get: { coordinator.whyConceptKey.map(IdentifiedString.init) },
                set: { coordinator.whyConceptKey = $0?.value }
            )) { item in
                // Sheets don't reliably inherit environmentObjects across the presentation
                // boundary, so re-inject the shared container + coordinator the modal reads.
                WhyModalView(conceptKey: item.value)
                    .environmentObject(container)
                    .environmentObject(coordinator)
                    .presentationDetents([.medium, .large])
                    .presentationDragIndicator(.visible)
            }
            // Session Swapper — native sheet.
            .sheet(item: Binding(
                get: { coordinator.swapperSessionId.map(IdentifiedString.init) },
                set: { coordinator.swapperSessionId = $0?.value }
            )) { item in
                SessionSwapperView(sessionId: item.value)
                    .environmentObject(container)
                    .environmentObject(coordinator)
                    .presentationDetents([.medium, .large])
                    .presentationDragIndicator(.visible)
            }

            // Brief branded launch surface over the stack; auto-advances.
            if !splashDone {
                AtlanSplashView().transition(.opacity)
            }
            }
            .task {
                try? await Task.sleep(nanoseconds: 800_000_000)
                withAnimation(.easeOut(duration: 0.45)) { splashDone = true }
            }
        }
    }

    @ViewBuilder
    private func destination(for route: AppRoute) -> some View {
        switch route {
        case .welcome: WelcomeView()
        case .calibration: CalibrationView()
        case .tunedSummary: TunedSummaryView()
        case .dashboard: TodayDashboardView()
        case .sessionDetail: SessionDetailView()
        case .workoutPrep: WorkoutPrepView()
        case .wetMode: WetModeView()
        case .settings: SettingsView()
        }
    }
}

/// Wraps a String so it can drive `.sheet(item:)`.
struct IdentifiedString: Identifiable {
    let value: String
    var id: String { value }
}

/// Brief branded launch surface — the Atlan wordmark on the calm Foam brand surface, fading in.
/// Native; the parent auto-advances past it. Defined in-file so it needs no `.xcodeproj` entry.
struct AtlanSplashView: View {
    @State private var shown = false

    var body: some View {
        ZStack {
            AtlanColors.foamWarm.ignoresSafeArea()
            VStack(spacing: AtlanSpacing.xs) {
                Text("atlan")
                    .font(.system(size: 44, weight: .semibold))
                    .foregroundColor(AtlanColors.abyss)
                RoundedRectangle(cornerRadius: 2)
                    .fill(AtlanColors.coral)
                    .frame(width: 52, height: 3)
            }
            .opacity(shown ? 1 : 0)
        }
        .accessibilityElement(children: .ignore)
        .accessibilityLabel("Atlan Performance")
        .onAppear { withAnimation(.easeOut(duration: 0.45)) { shown = true } }
    }
}
