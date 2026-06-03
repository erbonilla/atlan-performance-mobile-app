import SwiftUI
import Shared

/// Language Selection — choose EN or ES before account creation. No default selection, no flag
/// icons, no locale/IP inference. Both prompts shown so neither language is gated behind the other.
struct LanguageSelectionView: View {
    @EnvironmentObject private var coordinator: AppCoordinator

    var body: some View {
        ZStack {
            AtlanColors.foamWarm.ignoresSafeArea()
            VStack(spacing: AtlanSpacing.lg) {
                VStack(spacing: AtlanSpacing.xs) {
                    Text("atlan").font(.system(size: 40, weight: .semibold)).foregroundColor(AtlanColors.abyss)
                    Rectangle().fill(AtlanColors.coral).frame(width: 48, height: 3).clipShape(Capsule())
                }

                // Co-equal bilingual heading — neither language is gated behind the other (§4.1).
                VStack(spacing: AtlanSpacing.xs) {
                    Text("Choose your language")
                        .font(AtlanTypography.title).foregroundColor(AtlanColors.abyss)
                    Text("Elige tu idioma")
                        .font(AtlanTypography.title).foregroundColor(AtlanColors.tideDeep)
                }
                .multilineTextAlignment(.center)
                .padding(.top, AtlanSpacing.lg)

                VStack(spacing: AtlanSpacing.md) {
                    AtlanButton(title: "English") { coordinator.selectLanguage(.en) }
                        .accessibilityHint("Language option")
                    AtlanButton(title: "Español") { coordinator.selectLanguage(.es) }
                        .accessibilityHint("Opción de idioma")
                }
                .padding(.top, AtlanSpacing.lg)
            }
            .padding(AtlanSpacing.xl)
        }
        .navigationBarHidden(true)
    }
}
