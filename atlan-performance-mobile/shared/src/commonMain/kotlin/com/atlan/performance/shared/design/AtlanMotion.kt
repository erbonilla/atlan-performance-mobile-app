package com.atlan.performance.shared.design

/**
 * Shared motion intent. Calm, low-velocity transitions. Platform layers must respect Reduce Motion /
 * reduced-motion settings and provide non-animated alternatives. Durations in milliseconds.
 */
object AtlanMotion {
    const val quickMs = 120
    const val standardMs = 240
    const val calmMs = 360

    /** Large swipe threshold (pt/dp) before a Wet Mode gesture commits, to prevent accidental activation. */
    const val wetModeSwipeThreshold = 120
}
