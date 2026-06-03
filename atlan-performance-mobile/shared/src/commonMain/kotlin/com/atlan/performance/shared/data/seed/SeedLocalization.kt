package com.atlan.performance.shared.data.seed

import com.atlan.performance.shared.domain.model.Language
import com.atlan.performance.shared.localization.AtlanCopy
import com.atlan.performance.shared.localization.LocalizedStringKey

/**
 * Convenience accessor over the shared copy tables, exposed for seeding/preview. The authoritative
 * copy lives in EnglishCopy/SpanishCopy via AtlanCopy.
 */
object SeedLocalization {
    fun snapshot(language: Language): Map<String, String> =
        LocalizedStringKey.entries.associate { it.raw to AtlanCopy.get(it, language) }
}
