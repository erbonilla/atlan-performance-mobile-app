import SwiftUI
import Shared

/// Welcome — frames the app as adaptive and autonomy-supportive. Copy from the shared bilingual
/// layer; long Spanish strings wrap (fixedSize vertical), never shrink to fit.
struct WelcomeView: View {
    @EnvironmentObject private var coordinator: AppCoordinator

    var body: some View {
        let lang = coordinator.language
        ZStack {
            AtlanColors.foamWarm.ignoresSafeArea()
            VStack(alignment: .leading, spacing: AtlanSpacing.lg) {
                Text(localized(.onboardingWelcomeEyebrow, lang).uppercased())
                    .font(AtlanTypography.label).foregroundColor(AtlanColors.tideDeep)
                Text(localized(.onboardingWelcomeTitle, lang))
                    .font(AtlanTypography.display).foregroundColor(AtlanColors.abyss)
                    .fixedSize(horizontal: false, vertical: true)
                Text(localized(.onboardingWelcomeBody, lang))
                    .font(AtlanTypography.body).foregroundColor(AtlanColors.abyss)
                    .fixedSize(horizontal: false, vertical: true)
                Spacer()
                AtlanButton(title: localized(.onboardingWelcomeCta, lang)) {
                    coordinator.go(.calibration)
                }
            }
            .padding(AtlanSpacing.xl)
        }
    }
}
