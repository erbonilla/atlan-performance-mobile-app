import SwiftUI
import Shared

/// How It Works — a calm primer on set-based threshold training, with the "Pace Explanation" folded
/// in as its own section. Education on demand, never motivational pressure. Pushed from Settings;
/// uses the native NavigationStack back. Bilingual (inline EN/ES — not yet keyed).
struct HowItWorksView: View {
    @EnvironmentObject private var coordinator: AppCoordinator

    var body: some View {
        let es = coordinator.language == .es
        ScrollView {
            VStack(alignment: .leading, spacing: AtlanSpacing.md) {
                Text(es ? "Cómo funciona" : "How it works")
                    .font(AtlanTypography.display).foregroundColor(AtlanColors.abyss)

                section(
                    es ? "Entrenamiento por series" : "Set-based training",
                    es
                    ? "Cada sesión se divide en una serie de esfuerzos cortos y medidos con descanso entre ellos. Trabajar por series te deja sostener una intensidad de calidad más tiempo que un esfuerzo continuo."
                    : "Each session is broken into a handful of short, measured efforts with rest between them. Working in sets lets you hold quality intensity for longer than one continuous effort would."
                )
                section(
                    es ? "La estructura de 4 series" : "The 4-set structure",
                    es
                    ? "Un calentamiento fácil prepara el cuerpo; después vienen 4 series principales al umbral. El calentamiento no se cronometra — las 4 series sí."
                    : "An easy warm-up primes the body; then come 4 main sets at threshold. The warm-up isn't timed — the 4 main sets are."
                )
                section(
                    es ? "Ritmo objetivo" : "Target pace",
                    es
                    ? "El ritmo objetivo (p. ej. 1:35 / 100m) es el ritmo sostenible al umbral para esa serie. No es una prueba: si te quedas cerca y constante, el estímulo funciona. Pasarte de tiempo nunca se marca como fallo."
                    : "Target pace (e.g. 1:35 / 100m) is the sustainable threshold pace for that set. It isn't a test — staying near it and steady delivers the stimulus. Going over time is never marked as a failure."
                )
                section(
                    es ? "Descanso entre series" : "Rest between sets",
                    es
                    ? "Un descanso breve entre series deja que el esfuerzo siguiente vuelva a calidad. Puedes ajustar su duración en Ajustes, o saltarlo cuando estés listo."
                    : "A short rest between sets lets the next effort return to quality. You can adjust its length in Settings, or skip it when you're ready."
                )
                section(
                    es ? "Funciona sin conexión" : "Works offline",
                    es
                    ? "Las sesiones guardadas funcionan sin conexión, incluso con las manos mojadas. Tus resultados se guardan en el dispositivo y se sincronizan cuando vuelves a tener conexión."
                    : "Cached sessions work without connectivity, even with wet hands. Your results are saved on the device and sync when you're back online."
                )
            }
            .padding(AtlanSpacing.xl)
        }
        .background(AtlanColors.foamWarm.ignoresSafeArea())
    }

    private func section(_ title: String, _ body: String) -> some View {
        VStack(alignment: .leading, spacing: AtlanSpacing.xs) {
            Text(title.uppercased()).font(AtlanTypography.label).foregroundColor(AtlanColors.tideDeep)
            Text(body).foregroundColor(AtlanColors.abyss)
                .fixedSize(horizontal: false, vertical: true)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(AtlanSpacing.lg)
        .background(AtlanColors.paper)
        .clipShape(RoundedRectangle(cornerRadius: AtlanRadii.lg))
    }
}
