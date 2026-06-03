package com.atlan.performance.shared.localization

import com.atlan.performance.shared.domain.model.Language

/**
 * Shared copy access. Platform UI resolves strings through this in the initial setup; native string
 * catalogs can be layered later while these keys stay authoritative.
 *
 * ES is never silently substituted by EN: a missing key is a parity bug, surfaced by tests, not
 * hidden by a fallback to the other language.
 */
object AtlanCopy {

    fun get(key: LocalizedStringKey, language: Language): String {
        val table = when (language) {
            Language.EN -> EnglishCopy.values
            Language.ES -> SpanishCopy.values
        }
        // Returns the raw key id if absent so the gap is visible rather than masked as the other language.
        return table[key] ?: key.raw
    }

    /**
     * Resolves a templated key and substitutes positional `%1$s`, `%2$s`, … placeholders in order.
     * Keeps interpolation inside the bilingual layer so grammar/word-order stays per-language instead
     * of being reassembled with inline ternaries in platform UI.
     */
    fun format(key: LocalizedStringKey, language: Language, vararg args: String): String {
        var result = get(key, language)
        args.forEachIndexed { index, arg -> result = result.replace("%${index + 1}\$s", arg) }
        return result
    }

    /** True only if every key has a non-blank value in the given language. Used by parity checks. */
    fun hasFullCoverage(language: Language): Boolean {
        val table = when (language) {
            Language.EN -> EnglishCopy.values
            Language.ES -> SpanishCopy.values
        }
        return LocalizedStringKey.entries.all { table[it]?.isNotBlank() == true }
    }
}
