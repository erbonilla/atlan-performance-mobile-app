import SwiftUI

/// SwiftUI mirror of the shared color tokens (DESIGN_TOKENS.md).
/// Coral is rare (productive action / completion / high-signal accents). Tide marks science, calm
/// progress, selected state, and Why affordances. Never use Coral or red for disruption.
extension Color {
    init(hex: UInt32) {
        let r = Double((hex >> 16) & 0xFF) / 255.0
        let g = Double((hex >> 8) & 0xFF) / 255.0
        let b = Double(hex & 0xFF) / 255.0
        self.init(.sRGB, red: r, green: g, blue: b, opacity: 1.0)
    }
}

enum AtlanColors {
    static let abyss = Color(hex: 0x0B2A3C)
    static let abyssDeep = Color(hex: 0x061A26)
    static let tide = Color(hex: 0x0E8A9A)
    static let tideDeep = Color(hex: 0x0A6F7D)
    static let tideSoft = Color(hex: 0xBFE0E5)
    static let tidePale = Color(hex: 0xDDEEF1)
    static let coral = Color(hex: 0xFF6A3D)
    static let coralBright = Color(hex: 0xFF7E50)
    static let coralDeep = Color(hex: 0xE55428)
    static let foam = Color(hex: 0xECF7F8)
    static let foamWarm = Color(hex: 0xF4FAFB)
    static let paper = Color(hex: 0xFBFCFC)
}
