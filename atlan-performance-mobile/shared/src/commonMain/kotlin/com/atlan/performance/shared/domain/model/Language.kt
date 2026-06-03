package com.atlan.performance.shared.domain.model

/**
 * Product languages. EN and ES are equal first-class languages; ES is never a fallback for EN.
 * Language is chosen before account creation and never auto-selected by locale/IP.
 */
enum class Language {
    EN,
    ES
}
