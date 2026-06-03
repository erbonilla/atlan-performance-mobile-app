package com.atlan.performance.shared.domain.usecase

import com.atlan.performance.shared.domain.model.Language
import com.atlan.performance.shared.domain.repository.WhyConceptRepository
import com.atlan.performance.shared.presentation.WhyModalState

/**
 * Resolves Why content for a concept in the active language. A missing concept yields a calm
 * "content unavailable" state (requested = true, concept = null) — not an error. ES is never
 * silently replaced by EN.
 */
class GetWhyConceptUseCase(
    private val whyConceptRepository: WhyConceptRepository
) {
    suspend operator fun invoke(conceptKey: String, language: Language): WhyModalState {
        val concept = whyConceptRepository.getConcept(conceptKey, language)
        return WhyModalState(
            requested = true,
            concept = concept,
            cachedOffline = concept != null
        )
    }
}
