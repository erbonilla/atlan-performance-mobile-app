package com.atlan.performance.android.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.atlan.performance.android.design.AtlanBackButton
import com.atlan.performance.android.design.AtlanPalette
import com.atlan.performance.android.design.AtlanPill
import com.atlan.performance.android.design.AtlanType
import com.atlan.performance.shared.AtlanShared
import com.atlan.performance.shared.domain.model.CompletedSession
import com.atlan.performance.shared.domain.model.Language

/**
 * Workout History — finished sessions, newest first, from the local SQLDelight store. Calm record:
 * set counts, elapsed, optional effort. Partial sessions are shown as valid, never as failures. No
 * streaks, no totals-as-pressure. Empty state is reassuring.
 */
@Composable
fun HistoryScreen(shared: AtlanShared, language: Language, onBack: () -> Unit) {
    val es = language == Language.ES
    val history: List<CompletedSession>? by produceState<List<CompletedSession>?>(initialValue = null) {
        value = shared.getWorkoutHistory()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AtlanPalette.FoamWarm)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AtlanBackButton(onClick = onBack, contentDescription = if (es) "Atrás" else "Back")
        Text(if (es) "Historial" else "History", style = AtlanType.Display, color = AtlanPalette.Abyss)

        val items = history
        if (items == null) return@Column
        if (items.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AtlanPalette.Paper)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(if (es) "Aún no hay sesiones" else "No sessions yet",
                    style = AtlanType.Label, color = AtlanPalette.TideDeep)
                Text(
                    if (es) "Cuando termines una sesión, aparecerá aquí."
                    else "When you finish a session, it'll appear here.",
                    color = AtlanPalette.Abyss
                )
            }
        } else {
            items.forEach { HistoryRow(it, es) }
        }
    }
}

@Composable
private fun HistoryRow(item: CompletedSession, es: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AtlanPalette.Paper)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text(item.title, color = AtlanPalette.Abyss, style = AtlanType.Label,
                modifier = Modifier.weight(1f))
            Text(item.finishedAtIso.take(10), color = AtlanPalette.TideDeep)
        }
        val sets = if (es) "${item.completedSetCount} de ${item.totalSetCount} series"
            else "${item.completedSetCount} of ${item.totalSetCount} sets"
        Text("$sets · ${item.totalElapsedLabel}", color = AtlanPalette.TideDeep)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (!item.fullyCompleted) AtlanPill(if (es) "Parcial" else "Partial")
            item.perceivedEffort?.let { AtlanPill(effortLabel(it, es)) }
        }
    }
}

private fun effortLabel(key: String, es: Boolean): String = when (key) {
    "easy" -> if (es) "Fácil" else "Easy"
    "moderate" -> if (es) "Moderado" else "Moderate"
    "hard" -> if (es) "Duro" else "Hard"
    else -> key
}
