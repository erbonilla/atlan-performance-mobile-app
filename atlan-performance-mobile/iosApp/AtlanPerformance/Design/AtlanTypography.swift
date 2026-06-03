import SwiftUI

/// Atlan type hierarchy as Dynamic Type-friendly SwiftUI Font wrappers. Custom fonts are stubbed:
/// swap `.system` for bundled Fraunces (display) / Manrope (body) via `.custom(...)` once installed.
/// Spanish strings wrap; never shrink to fit (see view modifiers — no minimumScaleFactor below 1).
enum AtlanTypography {
    // TODO: replace .system with .custom("Fraunces", size:) when font assets are added.
    static let display = Font.system(.largeTitle, design: .serif).weight(.semibold)
    static let title = Font.system(.title2, design: .serif).weight(.semibold)
    static let body = Font.system(.body)
    static let label = Font.system(.caption).weight(.medium)
    /// Tabular numerics where available.
    static let numeric = Font.system(.title, design: .rounded).weight(.semibold).monospacedDigit()
    /// Extra-large, high-contrast Wet Mode numbers.
    static let wetMode = Font.system(size: 72, weight: .bold, design: .rounded).monospacedDigit()
}
