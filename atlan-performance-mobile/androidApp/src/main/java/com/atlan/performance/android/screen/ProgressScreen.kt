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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atlan.performance.android.design.AtlanBackButton
import com.atlan.performance.android.design.AtlanPalette
import com.atlan.performance.android.design.AtlanType
import com.atlan.performance.shared.AtlanShared
import com.atlan.performance.shared.domain.model.Language
import com.atlan.performance.shared.domain.model.ProgressOverview

/**
 * Progress Overview — calm, qualitative training summary from local history. No streaks, no goal
 * deficit, no red; "adjusted" (ended-early) sessions are valid, not failures. The closing line
 * reinforces the product stance: progress is a pattern, not a number to defend.
 */
@Composable
fun ProgressScreen(shared: AtlanShared, language: Language, onBack: () -> Unit) {
    val es = language == Language.ES
    val overview: ProgressOverview? by produceState<ProgressOverview?>(initialValue = null) {
        value = shared.getProgressOverview()
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
        Text(if (es) "Progreso" else "Progress", style = AtlanType.Display, color = AtlanPalette.Abyss)

        val o = overview ?: return@Column
        if (!o.hasAny) {
            Card {
                Text(if (es) "Aún no hay progreso" else "No progress yet",
                    style = AtlanType.Label, color = AtlanPalette.TideDeep)
                Text(
                    if (es) "Cuando completes sesiones, tu patrón aparecerá aquí."
                    else "When you complete sessions, your pattern will appear here.",
                    color = AtlanPalette.Abyss
                )
            }
            return@Column
        }

        Card {
            Text(if (es) "Constancia" else "Consistency", style = AtlanType.Label, color = AtlanPalette.TideDeep)
            Text(
                if (es) "${o.sessionCount} sesiones · ${o.setsCompleted} series"
                else "${o.sessionCount} sessions · ${o.setsCompleted} sets",
                fontSize = 22.sp, color = AtlanPalette.Abyss
            )
            Text(
                if (es) "${o.fullSessions} completas · ${o.adjustedSessions} ajustadas"
                else "${o.fullSessions} complete · ${o.adjustedSessions} adjusted",
                color = AtlanPalette.TideDeep
            )
        }

        if (o.effortLogged > 0) {
            Card {
                Text(if (es) "Esfuerzo percibido" else "Perceived effort",
                    style = AtlanType.Label, color = AtlanPalette.TideDeep)
                Text(
                    listOf(
                        (if (es) "Fácil" else "Easy") + " ${o.effortEasy}",
                        (if (es) "Moderado" else "Moderate") + " ${o.effortModerate}",
                        (if (es) "Duro" else "Hard") + " ${o.effortHard}"
                    ).joinToString(" · "),
                    color = AtlanPalette.Abyss
                )
            }
        }

        Text(
            if (es) "El progreso es un patrón, no un número que defender."
            else "Progress is a pattern, not a number to defend.",
            color = AtlanPalette.TideDeep, fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun Card(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AtlanPalette.Paper)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) { content() }
}
