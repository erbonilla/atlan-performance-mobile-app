import SwiftUI
import Shared

/// Atlan custom SwiftUI components. No component introduces streak / rank / flame / trophy / badge
/// pressure. Coral is reserved for productive action; Tide marks Why affordances.
///
/// Pattern coverage (atlan-mobile-design-patterns.md): every interactive control exposes a pressed
/// state (§9.2), meets the 44pt minimum touch target (§11.2), and never signals state through colour
/// alone (§11.5). Async actions get explicit loading + disabled states (§8.1, §10.5–§10.6).

/// Minimum touch target for any tappable control (§11.2 — iOS 44pt).
private let atlanMinTouchTarget: CGFloat = 44

/// Shared pressed-state treatment: a calm opacity + scale shift, no layout jitter (§9.2).
struct AtlanPressStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .opacity(configuration.isPressed ? 0.82 : 1.0)
            .scaleEffect(configuration.isPressed ? 0.98 : 1.0)
            .animation(.easeOut(duration: 0.12), value: configuration.isPressed)
    }
}

/// Primary CTA. Abyss pill by default; Coral only for productive action (e.g. start / complete).
/// Supports disabled (§10.5) and loading (§10.6 — spinner, preserved width, no double-submit) states.
struct AtlanButton: View {
    let title: String
    var coral: Bool = false
    var isEnabled: Bool = true
    var isLoading: Bool = false
    let action: () -> Void

    private var fill: Color {
        isEnabled ? (coral ? AtlanColors.coralBright : AtlanColors.abyss) : AtlanColors.tideSoft
    }

    var body: some View {
        Button(action: { if isEnabled && !isLoading { action() } }) {
            ZStack {
                // Keep the label in the layout (hidden) while loading so width never shifts (§9.4).
                Text(title)
                    .font(.headline)
                    .opacity(isLoading ? 0 : 1)
                if isLoading {
                    ProgressView().tint(AtlanColors.foam)
                }
            }
            .foregroundColor(AtlanColors.foam)
            .frame(maxWidth: .infinity, minHeight: atlanMinTouchTarget)
            .padding(.vertical, AtlanSpacing.md)
            .background(fill)
            .clipShape(Capsule())
        }
        .buttonStyle(AtlanPressStyle())
        .disabled(!isEnabled || isLoading)
        .accessibilityLabel(title)
        .accessibilityValue(isLoading ? "Loading" : "")
        .accessibilityHint(isEnabled ? "" : "Unavailable")
    }
}

/// Small status/label pill.
struct AtlanPill: View {
    let text: String
    var body: some View {
        Text(text)
            .font(.caption).bold()
            .foregroundColor(AtlanColors.tideDeep)
            .padding(.horizontal, AtlanSpacing.md)
            .padding(.vertical, AtlanSpacing.xs)
            .background(AtlanColors.tidePale)
            .clipShape(Capsule())
    }
}

/// Tide-outlined circular Why affordance with an italic i. Tide — never Coral. The visible ring stays
/// 24pt for calm density, but the tap target expands to the 44pt minimum (§11.2).
struct AtlanInfoButton: View {
    var accessibilityLabel: String = "Why — open explanation"
    let action: () -> Void
    var body: some View {
        Button(action: action) {
            Text("i")
                .italic().bold()
                .foregroundColor(AtlanColors.tide)
                .frame(width: 24, height: 24)
                .overlay(Circle().stroke(AtlanColors.tide, lineWidth: 1.5))
                .frame(width: atlanMinTouchTarget, height: atlanMinTouchTarget)
                .contentShape(Rectangle())
        }
        .buttonStyle(AtlanPressStyle())
        .accessibilityLabel(accessibilityLabel)
        .accessibilityAddTraits(.isButton)
    }
}

/// Single-select option row (Calibration, language-style choices). Selection is signalled by a
/// checkmark + border + fill, never colour alone (§11.5), and exposes the `.isSelected` trait so
/// VoiceOver announces state (§11.4). Whole row is tappable and meets the 44pt minimum (§8.2, §11.2).
struct AtlanSelectableRow: View {
    let title: String
    let isSelected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: AtlanSpacing.md) {
                Text(title)
                    .font(AtlanTypography.body)
                    .foregroundColor(isSelected ? AtlanColors.tideDeep : AtlanColors.abyss)
                    .fixedSize(horizontal: false, vertical: true)
                    .frame(maxWidth: .infinity, alignment: .leading)
                Image(systemName: isSelected ? "checkmark.circle.fill" : "circle")
                    .foregroundColor(isSelected ? AtlanColors.tide : AtlanColors.tideSoft)
            }
            .padding(AtlanSpacing.lg)
            .frame(minHeight: atlanMinTouchTarget)
            .background(isSelected ? AtlanColors.tidePale : AtlanColors.paper)
            .overlay(
                RoundedRectangle(cornerRadius: AtlanRadii.lg)
                    .stroke(isSelected ? AtlanColors.tide : AtlanColors.tideSoft,
                            lineWidth: isSelected ? 1.5 : 1)
            )
            .clipShape(RoundedRectangle(cornerRadius: AtlanRadii.lg))
        }
        .buttonStyle(AtlanPressStyle())
        .accessibilityAddTraits(isSelected ? [.isButton, .isSelected] : .isButton)
    }
}

/// Dashboard metric chip with inline Why affordance.
struct AtlanMetricChip: View {
    let chip: MetricChip
    let onWhy: (String) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: AtlanSpacing.xs) {
            HStack {
                Text(chip.title.uppercased())
                    .font(AtlanTypography.label).foregroundColor(AtlanColors.tideDeep)
                if let key = chip.whyConceptKey {
                    AtlanInfoButton(accessibilityLabel: "Why \(chip.title) — open explanation") { onWhy(key) }
                }
            }
            Text(chip.value).font(AtlanTypography.numeric).foregroundColor(AtlanColors.abyss)
            Text(chip.detail).font(AtlanTypography.body).foregroundColor(AtlanColors.abyss)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(AtlanSpacing.lg)
        .background(AtlanColors.paper)
        .clipShape(RoundedRectangle(cornerRadius: AtlanRadii.lg))
    }
}

/// Reusable calm error surface (§10.8). No blame, no red — Tide retry icon, explains the issue and
/// reassures that data is safe, offers Retry (and an optional safe exit). Used as a load-failure
/// fallback so unrecoverable states never strand the user.
struct AtlanErrorView: View {
    let title: String
    let message: String
    var retryTitle: String = "Retry"
    let onRetry: () -> Void
    var exitTitle: String? = nil
    var onExit: (() -> Void)? = nil

    var body: some View {
        VStack(spacing: AtlanSpacing.md) {
            Image(systemName: "arrow.clockwise.circle")
                .font(.system(size: 44)).foregroundColor(AtlanColors.tideDeep)
            Text(title).font(AtlanTypography.title).foregroundColor(AtlanColors.abyss)
                .multilineTextAlignment(.center)
                .fixedSize(horizontal: false, vertical: true)
            Text(message).foregroundColor(AtlanColors.tideDeep)
                .multilineTextAlignment(.center)
                .fixedSize(horizontal: false, vertical: true)
            AtlanButton(title: retryTitle) { onRetry() }
                .padding(.top, AtlanSpacing.sm)
            if let onExit = onExit, let exitTitle = exitTitle {
                Button { onExit() } label: {
                    Text(exitTitle).foregroundColor(AtlanColors.tideDeep)
                        .frame(maxWidth: .infinity, minHeight: 44).contentShape(Rectangle())
                }
                .buttonStyle(AtlanPressStyle())
            }
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, AtlanSpacing.xxxl)
    }
}

/// One half of the Wet Mode bottom action grid. Large touch zone, no precision tap. Coral only for
/// the productive Complete zone. Exposes an accessibility label so swipe is never the only path.
struct WetModeActionZone: View {
    let title: String
    let hint: String
    let accessibleLabel: String
    let productive: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            VStack(spacing: AtlanSpacing.xs) {
                Text(title).font(.system(size: 28, weight: .bold)).foregroundColor(AtlanColors.foam)
                Text(hint).foregroundColor(AtlanColors.foam)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(productive ? AtlanColors.coralBright : AtlanColors.abyss)
        }
        .buttonStyle(AtlanPressStyle())
        .accessibilityLabel(accessibleLabel)
        .accessibilityAddTraits(.isButton)
    }
}
