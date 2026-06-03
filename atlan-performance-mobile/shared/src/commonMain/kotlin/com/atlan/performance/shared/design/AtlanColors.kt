package com.atlan.performance.shared.design

/**
 * Shared color tokens as 0xAARRGGBB longs. Platform layers map these to native color types.
 * Source of truth: DESIGN_TOKENS.md.
 *
 * Discipline: Coral is rare (productive action / completion / high-signal accents only). Tide marks
 * science, calm progress, selected state, and Why affordances. Never use Coral or red for
 * errors, guilt, or missed-session states.
 */
object AtlanColors {
    const val Abyss: Long = 0xFF0B2A3C
    const val AbyssDeep: Long = 0xFF061A26
    const val Tide: Long = 0xFF0E8A9A
    const val TideDeep: Long = 0xFF0A6F7D
    const val TideSoft: Long = 0xFFBFE0E5
    const val TidePale: Long = 0xFFDDEEF1
    const val Coral: Long = 0xFFFF6A3D
    const val CoralBright: Long = 0xFFFF7E50
    const val CoralDeep: Long = 0xFFE55428
    const val Foam: Long = 0xFFECF7F8
    const val FoamWarm: Long = 0xFFF4FAFB
    const val Paper: Long = 0xFFFBFCFC
}
