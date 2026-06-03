package com.atlan.performance.android.screen.session

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atlan.performance.android.design.AtlanBackButton
import com.atlan.performance.android.design.AtlanButton
import com.atlan.performance.android.design.AtlanErrorScreen
import com.atlan.performance.android.design.AtlanInfoButton
import com.atlan.performance.android.design.AtlanPalette
import com.atlan.performance.android.design.AtlanPill
import com.atlan.performance.android.design.AtlanType
import com.atlan.performance.shared.AtlanShared
import com.atlan.performance.shared.domain.model.Language
import com.atlan.performance.shared.localization.AtlanCopy
import com.atlan.performance.shared.localization.LocalizedStringKey
import com.atlan.performance.shared.presentation.SessionDetailState

/**
 * Session Detail — shows session structure with minimal load. Tapping the `i` beside threshold opens
 * the Why Modal; Start session opens Wet Mode for this initial build. Works from cached data offline.
 */
@Composable
fun SessionDetailScreen(
    shared: AtlanShared,
    language: Language,
    onBack: () -> Unit,
    onWhy: (String) -> Unit,
    onStart: () -> Unit
) {
    val es = language == Language.ES
    var state by remember { mutableStateOf<SessionDetailState?>(null) }
    var loadFailed by remember { mutableStateOf(false) }
    var reloadKey by remember { mutableStateOf(0) }
    LaunchedEffect(reloadKey) {
        loadFailed = false
        val result = shared.getTodaySession()
        state = result
        loadFailed = result == null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AtlanPalette.FoamWarm)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AtlanBackButton(onClick = onBack, contentDescription = if (es) "Atrás" else "Back")

        val s = state
        if (s == null) {
            if (loadFailed) {
                AtlanErrorScreen(
                    title = if (es) "No pudimos cargar la sesión" else "We couldn't load the session",
                    message = if (es) "Tus datos están a salvo. Inténtalo de nuevo." else "Your data is safe. Try again.",
                    retryText = if (es) "Reintentar" else "Retry",
                    onRetry = { reloadKey++ }
                )
            }
            return@Column
        }
        val session = s.session

        if (session.offlineAvailable) AtlanPill(AtlanCopy.get(LocalizedStringKey.WET_MODE_OFFLINE_CACHED, language))

        Text(AtlanCopy.get(LocalizedStringKey.SESSION_DETAIL_DATE, language).uppercase(),
            style = AtlanType.Label, color = AtlanPalette.TideDeep)
        Text(session.title, style = AtlanType.Display, color = AtlanPalette.Abyss)
        Text("${session.distanceLabel} · ${session.durationEstimateLabel}", color = AtlanPalette.TideDeep)

        // Structure card.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(AtlanPalette.Paper)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            session.sets.forEach { set ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(set.label, color = AtlanPalette.Abyss)
                        set.targetPaceLabel?.let {
                            Text(AtlanCopy.format(LocalizedStringKey.WET_MODE_TARGET_PACE, language, it),
                                color = AtlanPalette.TideDeep, fontSize = 13.sp)
                        }
                    }
                    // Why affordance on the threshold main set.
                    if (set.targetPaceLabel != null) {
                        s.whyConceptKey?.let { AtlanInfoButton(onClick = { onWhy(it) }) }
                    }
                }
            }
        }

        Column(Modifier.weight(1f)) {}
        AtlanButton(
            text = if (language == Language.ES) "Empezar sesión" else "Start session",
            onClick = onStart,
            coral = true
        )
    }
}
