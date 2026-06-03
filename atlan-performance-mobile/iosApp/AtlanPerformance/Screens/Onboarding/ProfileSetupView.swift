import SwiftUI
import Shared

/// Profile Setup — a light, optional onboarding step. Captures a name and training level to tune
/// coaching tone; both can be skipped. No scoring, no judgement — these are settings. Stored locally
/// (UserDefaults via AppCoordinator); no account/backend. Bilingual (inline EN/ES — not yet keyed).
struct ProfileSetupView: View {
    @EnvironmentObject private var coordinator: AppCoordinator
    @State private var name: String = ""
    @State private var level: String = ""

    private let levels = [
        ("beginner", "Beginner", "Principiante"),
        ("intermediate", "Intermediate", "Intermedio"),
        ("advanced", "Advanced", "Avanzado")
    ]

    var body: some View {
        let isES = coordinator.language == .es
        ScrollView {
            VStack(alignment: .leading, spacing: AtlanSpacing.md) {
                Text((isES ? "Sobre ti" : "About you").uppercased())
                    .font(AtlanTypography.label).foregroundColor(AtlanColors.tideDeep)
                Text(isES ? "Ajusta tu entrenamiento" : "Tune your training")
                    .font(AtlanTypography.display).foregroundColor(AtlanColors.abyss)
                Text(isES ? "Opcional. Nos ayuda a ajustar el tono — nunca es una nota."
                          : "Optional. It helps us tune the tone — never a grade.")
                    .foregroundColor(AtlanColors.tideDeep)
                    .fixedSize(horizontal: false, vertical: true)

                TextField(isES ? "Tu nombre" : "Your name", text: $name)
                    .textFieldStyle(.plain)
                    .padding(AtlanSpacing.lg)
                    .background(AtlanColors.paper)
                    .clipShape(RoundedRectangle(cornerRadius: AtlanRadii.lg))

                Text(isES ? "Nivel de entrenamiento" : "Training level")
                    .font(AtlanTypography.label).foregroundColor(AtlanColors.tideDeep)
                    .padding(.top, AtlanSpacing.xs)
                ForEach(levels, id: \.0) { key, en, es in
                    levelRow(key: key, label: isES ? es : en)
                }

                Spacer(minLength: AtlanSpacing.xxl)
                AtlanButton(title: isES ? "Continuar" : "Continue", coral: false) {
                    coordinator.saveProfile(name: name.trimmingCharacters(in: .whitespaces), level: level)
                    coordinator.go(.tunedSummary)
                }
            }
            .padding(AtlanSpacing.xl)
        }
        .background(AtlanColors.foamWarm.ignoresSafeArea())
        .onAppear {
            name = coordinator.profileName
            level = coordinator.trainingLevel
        }
    }

    private func levelRow(key: String, label: String) -> some View {
        let selected = level == key
        return Button { level = selected ? "" : key } label: {
            HStack {
                Text((selected ? "✓ " : "") + label)
                    .fontWeight(selected ? .semibold : .regular)
                    .foregroundColor(selected ? AtlanColors.tideDeep : AtlanColors.abyss)
                Spacer()
            }
            .frame(minHeight: 48)
            .padding(AtlanSpacing.lg)
            .background(selected ? AtlanColors.tidePale : AtlanColors.paper)
            .clipShape(RoundedRectangle(cornerRadius: AtlanRadii.lg))
            .contentShape(Rectangle())
        }
        .buttonStyle(AtlanPressStyle())
        .accessibilityAddTraits(selected ? [.isButton, .isSelected] : .isButton)
    }
}
