import SwiftUI
import Shared

/// Settings — a light control surface. No account system in the first setup. Language switches the
/// whole app live (bilingual is first-class), and the Wet Mode preferences (haptics, keep-awake)
/// take effect immediately. Inferred onboarding settings are surfaced so they can become editable
/// later.
struct SettingsView: View {
    @EnvironmentObject private var coordinator: AppCoordinator

    private struct DisplayRow: Identifiable { let id = UUID(); let title: String; let value: String }

    var body: some View {
        let isES = coordinator.language == .es
        let displayRows: [DisplayRow] = isES ? [
            .init(title: "Cadencia de notificaciones", value: "Mínima"),
            .init(title: "Densidad de explicación", value: "Estándar"),
            .init(title: "Caché sin conexión", value: "Hoy + 7 días"),
            .init(title: "Acerca de Atlan", value: "v0.1")
        ] : [
            .init(title: "Notification cadence", value: "Minimal"),
            .init(title: "Explanation density", value: "Standard"),
            .init(title: "Offline cache", value: "Today + 7 days"),
            .init(title: "About Atlan", value: "v0.1")
        ]

        ScrollView {
            VStack(alignment: .leading, spacing: AtlanSpacing.md) {
                Text(isES ? "Ajustes" : "Settings")
                    .font(AtlanTypography.display).foregroundColor(AtlanColors.abyss)

                // Language — live, co-equal switch.
                paperRow {
                    Text(isES ? "Idioma" : "Language").foregroundColor(AtlanColors.abyss)
                    Spacer()
                    languageSegment(isES: isES)
                }

                // Wet Mode preferences — take effect immediately.
                toggleRow(isES ? "Vibración" : "Haptics", isOn: $coordinator.hapticsEnabled)
                toggleRow(isES ? "Pantalla siempre encendida" : "Keep screen awake",
                          isOn: $coordinator.keepScreenAwake)

                // Timer preference — rest window between sets; feeds the timer machine live.
                paperRow {
                    Text(isES ? "Descanso entre series" : "Rest between sets")
                        .foregroundColor(AtlanColors.abyss)
                    Spacer()
                    restSegment()
                }

                ForEach(displayRows) { row in
                    paperRow {
                        Text(row.title).foregroundColor(AtlanColors.abyss)
                        Spacer()
                        Text(row.value).foregroundColor(AtlanColors.tideDeep)
                            .multilineTextAlignment(.trailing)
                    }
                }

                // Education — opens the How It Works primer.
                Button { coordinator.go(.howItWorks) } label: {
                    paperRow {
                        Text(isES ? "Cómo funciona" : "How it works").foregroundColor(AtlanColors.abyss)
                        Spacer()
                        Text("›").foregroundColor(AtlanColors.tideDeep)
                    }
                }
                .buttonStyle(AtlanPressStyle())
            }
            .padding(AtlanSpacing.xl)
        }
        .background(AtlanColors.foamWarm.ignoresSafeArea())
    }

    // MARK: Building blocks

    private func paperRow<Content: View>(@ViewBuilder _ content: () -> Content) -> some View {
        HStack { content() }
            .frame(minHeight: 44)
            .padding(AtlanSpacing.lg)
            .background(AtlanColors.paper)
            .clipShape(RoundedRectangle(cornerRadius: AtlanRadii.lg))
    }

    private func toggleRow(_ title: String, isOn: Binding<Bool>) -> some View {
        paperRow {
            Toggle(isOn: isOn) {
                Text(title).foregroundColor(AtlanColors.abyss)
            }
            .tint(AtlanColors.tide)
        }
    }

    /// Rest-window options; tapping updates the preference (and persists) immediately.
    private func restSegment() -> some View {
        HStack(spacing: 0) {
            ForEach([30, 45, 60], id: \.self) { seconds in
                segment("\(seconds)s", selected: coordinator.restSeconds == seconds) {
                    coordinator.restSeconds = seconds
                }
            }
        }
        .background(AtlanColors.tidePale)
        .clipShape(Capsule())
    }

    /// Two co-equal language pills; tapping switches the whole app immediately.
    private func languageSegment(isES: Bool) -> some View {
        HStack(spacing: 0) {
            segment("English", selected: !isES) { coordinator.language = .en }
            segment("Español", selected: isES) { coordinator.language = .es }
        }
        .background(AtlanColors.tidePale)
        .clipShape(Capsule())
    }

    private func segment(_ title: String, selected: Bool, _ action: @escaping () -> Void) -> some View {
        Button(action: action) {
            Text(title)
                .font(.subheadline).bold()
                .foregroundColor(selected ? AtlanColors.foam : AtlanColors.tideDeep)
                .padding(.vertical, AtlanSpacing.sm)
                .padding(.horizontal, AtlanSpacing.md)
                .background(selected ? AtlanColors.abyss : Color.clear)
                .clipShape(Capsule())
        }
        .buttonStyle(AtlanPressStyle())
        .accessibilityAddTraits(selected ? [.isButton, .isSelected] : .isButton)
    }
}
