import SwiftUI
import Shared

/// Tuned Summary — shows inferred settings (each modeled so it can become editable later) and the
/// first-session preview, then navigates to the Today Dashboard.
struct TunedSummaryView: View {
    @EnvironmentObject private var coordinator: AppCoordinator

    private struct SettingRow: Identifiable { let id = UUID(); let title: String; let detail: String }

    var body: some View {
        let lang = coordinator.language
        let isES = lang == .es
        let rows: [SettingRow] = isES ? [
            .init(title: "Programación adaptativa", detail: "Activada. Absorberé las interrupciones antes de que se vuelvan una sesión perdida."),
            .init(title: "Profundidad a demanda", detail: "Activada. El \"por qué\" vive a un toque del término, con fuentes."),
            .init(title: "Notificaciones", detail: "Mínimas. Solo mañanas y domingos por la tarde."),
            .init(title: "Primera sesión · mañana", detail: "Piscina · Umbral · 1,000m")
        ] : [
            .init(title: "Adaptive scheduling", detail: "On. I'll absorb disruption before it becomes a missed session."),
            .init(title: "Depth on demand", detail: "On. The \"why\" lives one tap from the term, with sources."),
            .init(title: "Notifications", detail: "Minimal. Mornings and Sunday evenings only."),
            .init(title: "First session · tomorrow", detail: "Pool · Threshold · 1,000m")
        ]

        ZStack {
            AtlanColors.foamWarm.ignoresSafeArea()
            VStack(alignment: .leading, spacing: AtlanSpacing.md) {
                Text((isES ? "Afinado a tu semana" : "Tuned to your week").uppercased())
                    .font(AtlanTypography.label).foregroundColor(AtlanColors.tideDeep)
                Text(localized(.tunedTitle, lang))
                    .font(AtlanTypography.display).foregroundColor(AtlanColors.abyss)
                    .fixedSize(horizontal: false, vertical: true)

                ForEach(rows) { row in
                    VStack(alignment: .leading, spacing: AtlanSpacing.xs) {
                        Text(row.title).foregroundColor(AtlanColors.abyss)
                        Text(row.detail).foregroundColor(AtlanColors.tideDeep)
                            .fixedSize(horizontal: false, vertical: true)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(AtlanSpacing.lg)
                    .background(AtlanColors.paper)
                    .clipShape(RoundedRectangle(cornerRadius: AtlanRadii.lg))
                }

                Spacer()
                AtlanButton(title: localized(.tunedCta, lang), coral: true) {
                    coordinator.go(.dashboard)
                }
            }
            .padding(AtlanSpacing.xl)
        }
    }
}
