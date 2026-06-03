package com.atlan.performance.shared

import com.atlan.performance.shared.domain.model.Language
import com.atlan.performance.shared.localization.AtlanCopy
import com.atlan.performance.shared.localization.LocalizedStringKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * English and Spanish are first-class and at parity: every onboarding key exists in both, the Spanish
 * welcome title is non-empty, options have equal priority, and Spanish is never an optional fallback.
 */
class LocalizationParityTest {

    @Test
    fun english_and_spanish_have_full_parity() {
        assertTrue(AtlanCopy.hasFullCoverage(Language.EN), "English must define every key.")
        assertTrue(AtlanCopy.hasFullCoverage(Language.ES), "Spanish must define every key.")
    }

    @Test
    fun onboarding_keys_exist_in_both_languages() {
        val onboardingKeys = listOf(
            LocalizedStringKey.ONBOARDING_WELCOME_EYEBROW,
            LocalizedStringKey.ONBOARDING_WELCOME_TITLE,
            LocalizedStringKey.ONBOARDING_WELCOME_BODY,
            LocalizedStringKey.ONBOARDING_WELCOME_CTA
        )
        onboardingKeys.forEach { key ->
            val en = AtlanCopy.get(key, Language.EN)
            val es = AtlanCopy.get(key, Language.ES)
            // A returned raw key id means the string is missing in that language.
            assertTrue(en != key.raw && en.isNotBlank(), "Missing EN copy for ${key.raw}")
            assertTrue(es != key.raw && es.isNotBlank(), "Missing ES copy for ${key.raw}")
        }
    }

    @Test
    fun spanish_welcome_title_is_not_empty() {
        val esTitle = AtlanCopy.get(LocalizedStringKey.ONBOARDING_WELCOME_TITLE, Language.ES)
        assertTrue(esTitle.isNotBlank(), "Spanish welcome title must not be empty.")
    }

    @Test
    fun language_options_have_equal_priority() {
        // Both language choice labels resolve in both languages (no language is gated behind the other).
        Language.entries.forEach { lang ->
            assertTrue(AtlanCopy.get(LocalizedStringKey.LANGUAGE_CHOOSE_ENGLISH, lang).isNotBlank())
            assertTrue(AtlanCopy.get(LocalizedStringKey.LANGUAGE_CHOOSE_SPANISH, lang).isNotBlank())
        }
    }

    @Test
    fun spanish_is_not_a_fallback_to_english() {
        // ES copy for distinctly-worded keys must differ from EN (proves it is authored, not echoed).
        val esBody = AtlanCopy.get(LocalizedStringKey.ONBOARDING_WELCOME_BODY, Language.ES)
        val enBody = AtlanCopy.get(LocalizedStringKey.ONBOARDING_WELCOME_BODY, Language.EN)
        assertTrue(esBody != enBody, "Spanish body must be authored, not an English fallback.")

        val esCta = AtlanCopy.get(LocalizedStringKey.ONBOARDING_WELCOME_CTA, Language.ES)
        assertEquals("Comenzar", esCta, "Spanish CTA must be the authored Spanish value.")
    }
}
