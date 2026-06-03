package com.atlan.performance.shared.design

/**
 * Shared typographic intent. Actual fonts/sizes are implemented natively (SwiftUI Font wrappers,
 * Compose typography) — this enum only fixes the hierarchy so both platforms agree. See
 * DESIGN_TOKENS.md. Spanish strings wrap; they never shrink to fit.
 */
enum class AtlanTextRole {
    /** Calm editorial display type (Fraunces / native display fallback). */
    Display,
    /** High-legibility sans (Manrope / native system fallback). */
    Body,
    /** Tabular numeric where available. */
    Numeric,
    /** Compact uppercase metadata / state labels. */
    Label,
    /** Extra-large, high-contrast Wet Mode numbers. */
    WetMode
}
