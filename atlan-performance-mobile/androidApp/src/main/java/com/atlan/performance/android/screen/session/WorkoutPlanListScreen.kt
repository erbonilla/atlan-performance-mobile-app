package com.atlan.performance.android.screen.session

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.atlan.performance.android.design.AtlanBackButton
import com.atlan.performance.android.design.AtlanPalette
import com.atlan.performance.android.design.AtlanPill
import com.atlan.performance.android.design.AtlanType
import com.atlan.performance.shared.AtlanShared
import com.atlan.performance.shared.domain.model.Language
import com.atlan.performance.shared.domain.model.SessionStatus
import com.atlan.performance.shared.domain.model.TrainingSession
import com.atlan.performance.shared.domain.model.TrainingWeek

/**
 * Workout Plan List — the current week's sessions (inventory §5.3). Completed and upcoming sessions
 * are equal, calm states; only today's session is actionable (opens Session Detail). No streak,
 * no "missed", no red. Falls back to a calm empty state if the week has no sessions.
 */
@Composable
fun WorkoutPlanListScreen(
    shared: AtlanShared,
    language: Language,
    onBack: () -> Unit,
    onOpenToday: () -> Unit
) {
    val es = language == Language.ES
    val week: TrainingWeek? by produceState<TrainingWeek?>(initialValue = null) {
        value = shared.getTrainingPlan()
    }
    val todayId: String? by produceState<String?>(initialValue = null) {
        value = shared.getTodaySession()?.session?.id
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
        Text(if (es) "Tu plan" else "Your plan", style = AtlanType.Display, color = AtlanPalette.Abyss)

        val w = week
        val sessions = w?.sessions.orEmpty()
        if (w != null) {
            Text(
                (if (es) "Semana ${w.index} de ${w.totalWeeks} · En curso"
                else "Week ${w.index} of ${w.totalWeeks} · On track"),
                color = AtlanPalette.TideDeep
            )
        }

        if (sessions.isEmpty()) {
            // Calm empty state — never alarming; this week simply has nothing scheduled yet.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AtlanPalette.Paper)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(if (es) "Nada programado esta semana" else "Nothing scheduled this week",
                    style = AtlanType.Label, color = AtlanPalette.TideDeep)
                Text(
                    if (es) "Cuando haya sesiones, aparecerán aquí. Disfruta el descanso."
                    else "When sessions are scheduled, they'll appear here. Enjoy the rest.",
                    color = AtlanPalette.Abyss
                )
            }
        } else {
            sessions.forEach { session ->
                PlanRow(
                    session = session,
                    isToday = session.id == todayId,
                    es = es,
                    onOpen = if (session.id == todayId) onOpenToday else null
                )
            }
        }
    }
}

@Composable
private fun PlanRow(
    session: TrainingSession,
    isToday: Boolean,
    es: Boolean,
    onOpen: (() -> Unit)?
) {
    val statusLabel = when {
        session.status == SessionStatus.COMPLETED -> if (es) "Completada" else "Completed"
        isToday -> if (es) "Hoy" else "Today"
        else -> if (es) "Próxima" else "Upcoming"
    }

    val base = Modifier
        .fillMaxWidth()
        .heightIn(min = 64.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(AtlanPalette.Paper)
    val rowModifier = if (onOpen != null) {
        base.clickable(onClick = onOpen).semantics { role = Role.Button }
    } else base

    Row(
        modifier = rowModifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(session.title, color = AtlanPalette.Abyss, style = AtlanType.Label)
            Text("${session.distanceLabel} · ${session.durationEstimateLabel}",
                color = AtlanPalette.TideDeep)
            if (session.offlineAvailable) {
                Text(if (es) "Sin conexión · Listo" else "Offline · Ready",
                    color = AtlanPalette.TideDeep, style = AtlanType.Label)
            }
        }
        AtlanPill(statusLabel)
    }
}
