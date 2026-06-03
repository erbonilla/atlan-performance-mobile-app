package com.atlan.performance.shared.data.fake

import com.atlan.performance.shared.data.seed.SeedWhyConcepts
import com.atlan.performance.shared.domain.model.Language
import com.atlan.performance.shared.domain.model.WhyConcept
import com.atlan.performance.shared.domain.repository.WhyConceptRepository

/** In-memory Why content cache. Returns null when a concept/language pair is uncached. */
class FakeWhyConceptRepository(
    concepts: List<WhyConcept> = SeedWhyConcepts.concepts
) : WhyConceptRepository {

    private val byKeyAndLanguage: Map<Pair<String, Language>, WhyConcept> =
        concepts.associateBy { it.conceptKey to it.language }

    override suspend fun getConcept(conceptKey: String, language: Language): WhyConcept? =
        byKeyAndLanguage[conceptKey to language]
}
