import SwiftUI
import Shared

/// Calibration — tunes coaching posture without scoring or judging. Selected option uses TidePale +
/// Tide. No "good/bad athlete" framing; answers become settings, not scores. One example question is
/// scaffolded for the initial build.
struct CalibrationView: View {
    @EnvironmentObject private var coordinator: AppCoordinator
    @State private var selected: Int? = nil

    var body: some View {
        let lang = coordinator.language
        let isES = lang == .es
        let question = isES ? "¿Qué te lleva a entrenar la mayoría de las semanas?"
                            : "What pulls you to training most weeks?"
        let options = isES
            ? ["El espacio que me da del trabajo", "La búsqueda de mejorar", "La estructura para mi semana"]
            : ["The space it gives me from work", "The pursuit of getting better", "The structure for my week"]

        ZStack {
            AtlanColors.foamWarm.ignoresSafeArea()
            VStack(alignment: .leading, spacing: AtlanSpacing.md) {
                HStack {
                    Spacer()
                    AtlanPill(text: "2 of 4")
                }
                Text(localized(.calibrationSubtitle, lang).uppercased())
                    .font(AtlanTypography.label).foregroundColor(AtlanColors.tideDeep)
                Text(localized(.calibrationTitle, lang))
                    .font(AtlanTypography.display).foregroundColor(AtlanColors.abyss)
                    .fixedSize(horizontal: false, vertical: true)

                Text(question).font(.title3).foregroundColor(AtlanColors.abyss)
                    .padding(.top, AtlanSpacing.md)
                    .fixedSize(horizontal: false, vertical: true)

                ForEach(Array(options.enumerated()), id: \.offset) { index, option in
                    AtlanSelectableRow(title: option, isSelected: index == selected) {
                        selected = index
                    }
                }

                Spacer()
                // Continue stays enabled (answers are settings, not a gate); selection is optional.
                AtlanButton(title: isES ? "Continuar" : "Continue") {
                    coordinator.go(.tunedSummary)
                }
            }
            .padding(AtlanSpacing.xl)
        }
    }
}
