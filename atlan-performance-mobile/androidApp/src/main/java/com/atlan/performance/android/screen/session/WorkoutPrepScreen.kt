package com.atlan.performance.android.screen.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.atlan.performance.android.design.AtlanBackButton
import com.atlan.performance.android.design.AtlanButton
import com.atlan.performance.android.design.AtlanPalette
import com.atlan.performance.android.design.AtlanPill
import com.atlan.performance.android.design.AtlanType
import com.atlan.performance.shared.AtlanShared
import com.atlan.performance.shared.domain.model.Language
import com.atlan.performance.shared.presentation.SessionDetailState

/**
 * Workout Prep — the final readiness step before the active 4-set timer (inventory §8.4). Confirms
 * the session shape and offline availability, offers a calm warm-up reminder, then starts Wet Mode.
 * Loads from cache; offline-first. Back returns to Session Detail without losing anything.
 */
@Composable
fun WorkoutPrepScreen(
    shared: AtlanShared,
    language: Language,
    onBack: () -> Unit,
    onBegin: () -> Unit
) {
    val es = language == Language.ES
    val state: SessionDetailState? by produceState<SessionDetailState?>(initialValue = null) {
        value = shared.getTodaySession()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AtlanPalette.FoamWarm)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AtlanBackButton(onClick = onBack, contentDescription = if (es) "Atrás" else "Back")

        val s = state ?: return@Column
        val session = s.session
        val mainSets = session.sets.filter { it.targetPaceLabel != null }
        val pace = mainSets.firstOrNull()?.targetPaceLabel ?: "—"
        val perSet = mainSets.firstOrNull()?.distanceLabel ?: session.distanceLabel

        if (session.offlineAvailable) AtlanPill(if (es) "Sin conexión · Listo" else "Offline · Ready")
        Text((if (es) "Listo para empezar" else "Ready to begin").uppercase(),
            style = AtlanType.Label, color = AtlanPalette.TideDeep)
        Text(session.title, style = AtlanType.Display, color = AtlanPalette.Abyss)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AtlanPalette.Paper)
                .padding(16.dp)
        ) {
            PrepRow(if (es) "Tipo" else "Type", "Threshold")
            PrepRow(if (es) "Series" else "Sets", "${mainSets.size} × $perSet")
            PrepRow(if (es) "Ritmo objetivo" else "Target pace", pace)
            PrepRow(if (es) "Tiempo estimado" else "Estimated time", session.durationEstimateLabel)
        }

        Text(
            if (es) "Tómate un momento para calentar. El cronómetro empieza cuando estés listo."
            else "Take a moment to warm up. The timer starts when you're ready.",
            color = AtlanPalette.TideDeep,
            modifier = Modifier.padding(top = 4.dp)
        )

        Column(Modifier.weight(1f)) {}
        AtlanButton(text = if (es) "Empezar sesión" else "Begin session", onClick = onBegin, coral = true)
    }
}

@Composable
private fun PrepRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().heightIn(min = 44.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = AtlanPalette.Abyss, modifier = Modifier.weight(1f))
        Text(value, color = AtlanPalette.TideDeep)
    }
}
