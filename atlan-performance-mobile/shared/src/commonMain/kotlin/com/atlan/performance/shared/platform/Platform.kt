package com.atlan.performance.shared.platform

/**
 * Minimal platform seam. Expanded later for platform time, storage, and capability flags. For the
 * initial setup it only identifies the host so the shared layer can be smoke-tested from each app.
 */
expect class Platform() {
    val name: String
}
