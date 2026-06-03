package com.atlan.performance.android.screen.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atlan.performance.android.design.AtlanButton
import com.atlan.performance.android.design.AtlanInfoButton
import com.atlan.performance.android.design.AtlanMetricChip
import com.atlan.performance.android.design.AtlanPalette
import com.atlan.performance.android.design.AtlanType
import com.atlan.performance.shared.AtlanShared
import com.atlan.performance.shared.domain.model.Language
import com.atlan.performance.shared.domain.model.SessionProgress
import com.atlan.performance.shared.presentation.DashboardState
import com.atlan.performance.shared.presentation.WeeklyArcState
import kotlinx.coroutines.launch

/**
 * Today Dashboard — calm, answers only "what is today?" and "is the week on track?". No streaks,
 * leaderboards, peer ranking, notification badges, red missed rows, or behind-plan messaging.
 */
@Composable
fun TodayDashboardScreen(
    shared: AtlanShared,
    language: Language,
    onStartSession: () -> Unit,
    onWhy: (String) -> Unit,
    onOpenSwapper: (String) -> Unit,
    onViewPlan: () -> Unit,
    onResume: () -> Unit,
    onSettings: () -> Unit
) {
    val state: DashboardState? by produceState<DashboardState?>(initialValue = null) {
        value = shared.getTodayDashboard()
    }
    // A saved in-progress session (survived process death) → offer calm Resume / Discard.
    val scope = rememberCoroutineScope()
    var resumable by remember { mutableStateOf<SessionProgress?>(null) }
    LaunchedEffect(Unit) { resumable = shared.loadSessionProgress() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AtlanPalette.FoamWarm)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quiet header.
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("atlan", fontSize = 24.sp, color = AtlanPalette.Abyss)
            Column(Modifier.weight(1f)) {}
            Text(state?.dateLabel ?: "", color = AtlanPalette.TideDeep)
            Text(
                "⚙",
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .clickable(onClick = onSettings)
                    .wrapContentSize(Alignment.Center)
                    .padding(start = 8.dp)
                    .semantics {
                        contentDescription = if (language == Language.ES) "Ajustes" else "Settings"
                        role = Role.Button
                    },
                color = AtlanPalette.TideDeep,
                fontSize = 20.sp
            )
        }

        // Resume banner — a calm offer to pick up an interrupted session, never a "you stopped" nudge.
        resumable?.let { p ->
            val es = language == Language.ES
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AtlanPalette.TidePale)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    (if (es) "Reanudar sesión · Serie ${p.setIndex} de ${p.setCount}"
                    else "Resume session · Set ${p.setIndex} of ${p.setCount}"),
                    color = AtlanPalette.TideDeep
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    AtlanButton(text = if (es) "Reanudar" else "Resume", onClick = onResume, coral = true)
                    Text(
                        if (es) "Descartar" else "Discard",
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .clickable {
                                scope.launch { shared.clearSessionProgress(p.sessionId) }
                                resumable = null
                            }
                            .wrapContentSize(Alignment.Center)
                            .semantics { role = Role.Button },
                        color = AtlanPalette.TideDeep
                    )
                }
            }
        }

        val s = state ?: return@Column

        // Today hero card.
        s.todaySession?.let { session ->
            // No whole-card tap: the Start and Adjust controls below are the clear targets (§9.1).
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(AtlanPalette.Abyss)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Today · Pool".uppercase(), style = AtlanType.Label, color = AtlanPalette.TideSoft)
                Text(session.distanceLabel, fontSize = 44.sp, color = AtlanPalette.Foam)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Threshold · ${session.durationEstimateLabel}", color = AtlanPalette.TideSoft)
                    Text("  ", fontSize = 6.sp)
                    s.todayWhyConceptKey?.let { AtlanInfoButton(onClick = { onWhy(it) }) }
                }
                AtlanButton(
                    text = "Start session →",
                    onClick = onStartSession,
                    coral = true,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    if (language == Language.ES) "Ajustar hoy" else "Adjust today",
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .heightIn(min = 48.dp)
                        .clickable { onOpenSwapper(session.id) }
                        .wrapContentSize(Alignment.CenterStart)
                        .semantics { role = Role.Button },
                    color = AtlanPalette.TideSoft
                )
            }
        }

        // Weekly arc card.
        WeeklyArcCard(s.weeklyArc)

        // Quiet entry to the full week's plan.
        Text(
            if (language == Language.ES) "Ver el plan de la semana" else "View this week's plan",
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clickable(onClick = onViewPlan)
                .wrapContentSize(Alignment.CenterStart)
                .semantics { role = Role.Button },
            color = AtlanPalette.TideDeep
        )

        // Metric chips.
        s.metricChips.forEach { chip ->
            AtlanMetricChip(chip = chip, onWhy = onWhy, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun WeeklyArcCard(arc: WeeklyArcState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(AtlanPalette.Paper)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(arc.label.uppercase(), style = AtlanType.Label, color = AtlanPalette.TideDeep)
        Text(arc.status, fontSize = 18.sp, color = AtlanPalette.Abyss)

        // Calm line chart placeholder with a single Coral marker for the current week.
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
        ) {
            if (arc.points.size < 2) return@Canvas
            val stepX = size.width / (arc.points.size - 1)
            val pts = arc.points.mapIndexed { i, p ->
                Offset(i * stepX, size.height - (p * size.height))
            }
            for (i in 0 until pts.size - 1) {
                drawLine(
                    color = androidx.compose.ui.graphics.Color(0xFF0E8A9A),
                    start = pts[i], end = pts[i + 1], strokeWidth = 6f
                )
            }
            // Coral marker for the current week.
            val marker = pts.getOrNull(arc.currentWeekMarker) ?: pts.last()
            drawCircle(color = androidx.compose.ui.graphics.Color(0xFFFF6A3D), radius = 10f, center = marker)
        }
    }
}
