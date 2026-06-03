import SwiftUI
import Shared

/// Why Modal — native sheet, scrollable, with a close affordance (drag indicator + button). Tide
/// marks the concept label and mechanism block. No motivational copy inside the science; every
/// mechanism is backed by the reference. Missing content is handled calmly (no error styling).
struct WhyModalView: View {
    @EnvironmentObject private var container: SharedContainer
    @EnvironmentObject private var coordinator: AppCoordinator
    @Environment(\.dismiss) private var dismiss
    let conceptKey: String
    @State private var state: WhyModalState?

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: AtlanSpacing.md) {
                HStack {
                    Spacer()
                    Button { dismiss() } label: {
                        Image(systemName: "xmark.circle.fill")
                            .font(.title3)
                            .foregroundColor(AtlanColors.tideDeep)
                            .frame(width: 44, height: 44)
                            .contentShape(Rectangle())
                    }
                    .buttonStyle(AtlanPressStyle())
                    .accessibilityLabel(coordinator.language == .es ? "Cerrar" : "Close")
                }

                if let concept = state?.concept {
                    Text(concept.eyebrow.uppercased())
                        .font(AtlanTypography.label).foregroundColor(AtlanColors.tide)
                    Text(concept.title).font(AtlanTypography.display).foregroundColor(AtlanColors.abyss)
                    Text(concept.body).foregroundColor(AtlanColors.abyss)
                        .fixedSize(horizontal: false, vertical: true)

                    Text(coordinator.language == .es ? "Mecanismo" : "Mechanism")
                        .font(AtlanTypography.label).foregroundColor(AtlanColors.tide)
                    ForEach(concept.mechanisms, id: \.self) { mechanism in
                        Text("• \(mechanism)").foregroundColor(AtlanColors.tideDeep)
                            .fixedSize(horizontal: false, vertical: true)
                    }

                    Text(coordinator.language == .es ? "Referencia" : "Reference")
                        .font(AtlanTypography.label).foregroundColor(AtlanColors.tide)
                    Text(concept.reference).font(.footnote).foregroundColor(AtlanColors.abyss)
                        .fixedSize(horizontal: false, vertical: true)
                } else if state != nil {
                    Text(coordinator.language == .es
                         ? "Contenido no disponible sin conexión. Volverá cuando se sincronice."
                         : "This explanation isn't cached yet. It'll appear once synced.")
                        .foregroundColor(AtlanColors.tideDeep)
                } else {
                    ProgressView().tint(AtlanColors.tide)
                }
            }
            .padding(AtlanSpacing.xl)
        }
        .background(AtlanColors.paper.ignoresSafeArea())
        .task { state = await container.whyConcept(key: conceptKey, language: coordinator.language) }
    }
}
