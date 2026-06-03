package com.atlan.performance.shared.data.seed

import com.atlan.performance.shared.domain.model.Language
import com.atlan.performance.shared.domain.model.WhyConcept

/**
 * Seed Why content. Precise, sourced, no motivational hype. English is verbatim from the product
 * concept; Spanish is an emotional-parity rendering of the same science (not a fallback).
 */
object SeedWhyConcepts {

    private val thresholdEn = WhyConcept(
        id = "why-threshold-en",
        conceptKey = "threshold",
        language = Language.EN,
        eyebrow = "Depth · Threshold",
        title = "Why threshold",
        body = "Threshold intervals develop lactate clearance — the rate at which your body processes " +
            "accumulating lactate during sustained efforts. Repeating this stimulus at the boundary " +
            "between sustainable and unsustainable pace raises the work rate you can hold for an " +
            "extended duration.\n\n" +
            "These adaptations distinguish trained endurance athletes from untrained populations. The " +
            "standard prescription is 4–6 repetitions of 5–15 minutes at threshold pace, with short " +
            "recoveries — the partial recovery is the point.",
        mechanisms = listOf(
            "Increased mitochondrial density in slow-twitch fibers",
            "Elevated capillarization around working muscle",
            "Improved enzymatic activity for aerobic energy production"
        ),
        reference = "Maglischo, E. W. (2003). Swimming Fastest (2nd ed.). Champaign, IL: Human Kinetics, pp. 348–356."
    )

    private val thresholdEs = WhyConcept(
        id = "why-threshold-es",
        conceptKey = "threshold",
        language = Language.ES,
        eyebrow = "Profundidad · Umbral",
        title = "Por qué umbral",
        body = "Los intervalos de umbral desarrollan el aclaramiento de lactato — la velocidad a la que " +
            "tu cuerpo procesa el lactato que se acumula durante esfuerzos sostenidos. Repetir este " +
            "estímulo en la frontera entre el ritmo sostenible y el insostenible eleva la intensidad de " +
            "trabajo que puedes mantener durante un tiempo prolongado.\n\n" +
            "Estas adaptaciones distinguen a los atletas de resistencia entrenados de la población no " +
            "entrenada. La prescripción estándar es de 4 a 6 repeticiones de 5 a 15 minutos a ritmo de " +
            "umbral, con recuperaciones cortas — la recuperación parcial es el punto.",
        mechanisms = listOf(
            "Mayor densidad mitocondrial en fibras de contracción lenta",
            "Mayor capilarización alrededor del músculo activo",
            "Mejor actividad enzimática para la producción de energía aeróbica"
        ),
        reference = "Maglischo, E. W. (2003). Swimming Fastest (2.ª ed.). Champaign, IL: Human Kinetics, pp. 348–356."
    )

    val concepts: List<WhyConcept> = listOf(thresholdEn, thresholdEs)
}
