package com.atlan.performance.shared.domain.repository

import com.atlan.performance.shared.domain.model.Language
import com.atlan.performance.shared.domain.model.WhyConcept

interface WhyConceptRepository {
    /** Returns cached Why content for a concept in the requested language, or null if unavailable. */
    suspend fun getConcept(conceptKey: String, language: Language): WhyConcept?
}
