package com.atlan.performance.android.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.atlan.performance.android.design.AtlanBackButton
import com.atlan.performance.android.design.AtlanPalette
import com.atlan.performance.android.design.AtlanType
import com.atlan.performance.shared.domain.model.Language

/**
 * How It Works — a calm primer on set-based threshold training, including what target pace means
 * (the inventory's "Pace Explanation" is folded in here as its own section). Education on demand,
 * never motivational pressure. Reachable from Settings. Bilingual (inline EN/ES — not yet keyed).
 */
@Composable
fun HowItWorksScreen(language: Language, onBack: () -> Unit) {
    val es = language == Language.ES

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AtlanPalette.FoamWarm)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AtlanBackButton(onClick = onBack, contentDescription = if (es) "Atrás" else "Back")
        Text(if (es) "Cómo funciona" else "How it works",
            style = AtlanType.Display, color = AtlanPalette.Abyss)

        Section(
            if (es) "Entrenamiento por series" else "Set-based training",
            if (es)
                "Cada sesión se divide en una serie de esfuerzos cortos y medidos con descanso entre ellos. Trabajar por series te deja sostener una intensidad de calidad más tiempo que un esfuerzo continuo."
            else
                "Each session is broken into a handful of short, measured efforts with rest between them. Working in sets lets you hold quality intensity for longer than one continuous effort would."
        )
        Section(
            if (es) "La estructura de 4 series" else "The 4-set structure",
            if (es)
                "Un calentamiento fácil prepara el cuerpo; después vienen 4 series principales al umbral. El calentamiento no se cronometra — las 4 series sí."
            else
                "An easy warm-up primes the body; then come 4 main sets at threshold. The warm-up isn't timed — the 4 main sets are."
        )
        Section(
            if (es) "Ritmo objetivo" else "Target pace",
            if (es)
                "El ritmo objetivo (p. ej. 1:35 / 100m) es el ritmo sostenible al umbral para esa serie. No es una prueba: si te quedas cerca y constante, el estímulo funciona. Pasarte de tiempo nunca se marca como fallo."
            else
                "Target pace (e.g. 1:35 / 100m) is the sustainable threshold pace for that set. It isn't a test — staying near it and steady delivers the stimulus. Going over time is never marked as a failure."
        )
        Section(
            if (es) "Descanso entre series" else "Rest between sets",
            if (es)
                "Un descanso breve entre series deja que el esfuerzo siguiente vuelva a calidad. Puedes ajustar su duración en Ajustes, o saltarlo cuando estés listo."
            else
                "A short rest between sets lets the next effort return to quality. You can adjust its length in Settings, or skip it when you're ready."
        )
        Section(
            if (es) "Funciona sin conexión" else "Works offline",
            if (es)
                "Las sesiones guardadas funcionan sin conexión, incluso con las manos mojadas. Tus resultados se guardan en el dispositivo y se sincronizan cuando vuelves a tener conexión."
            else
                "Cached sessions work without connectivity, even with wet hands. Your results are saved on the device and sync when you're back online."
        )
    }
}

@Composable
private fun Section(title: String, body: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AtlanPalette.Paper)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(title.uppercase(), style = AtlanType.Label, color = AtlanPalette.TideDeep)
        Text(body, color = AtlanPalette.Abyss)
    }
}
