package com.atlan.performance.android.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.atlan.performance.android.design.AtlanButton
import com.atlan.performance.android.design.AtlanPalette
import com.atlan.performance.android.design.AtlanType
import com.atlan.performance.shared.design.AtlanRadii
import com.atlan.performance.shared.design.AtlanSpacing
import com.atlan.performance.shared.domain.model.Language
import com.atlan.performance.shared.localization.AtlanCopy
import com.atlan.performance.shared.localization.LocalizedStringKey

/**
 * Tuned Summary — shows inferred settings (each modeled so it can become editable later) and the
 * first session preview, then navigates to the Today Dashboard.
 */
@Composable
fun TunedSummaryScreen(language: Language, onSeeFirstSession: () -> Unit) {
    val es = language == Language.ES
    fun copy(key: LocalizedStringKey) = AtlanCopy.get(key, language)

    data class Setting(val title: String, val detail: String)
    val rows = if (es) listOf(
        Setting("Programación adaptativa", "Activada. Absorberé las interrupciones antes de que se vuelvan una sesión perdida."),
        Setting("Profundidad a demanda", "Activada. El \"por qué\" vive a un toque del término, con fuentes."),
        Setting("Notificaciones", "Mínimas. Solo mañanas y domingos por la tarde."),
        Setting("Primera sesión · mañana", "Piscina · Umbral · 1,000m")
    ) else listOf(
        Setting("Adaptive scheduling", "On. I'll absorb disruption before it becomes a missed session."),
        Setting("Depth on demand", "On. The \"why\" lives one tap from the term, with sources."),
        Setting("Notifications", "Minimal. Mornings and Sunday evenings only."),
        Setting("First session · tomorrow", "Pool · Threshold · 1,000m")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AtlanPalette.FoamWarm)
            .padding(AtlanSpacing.xl.dp)
    ) {
        Text(
            (if (es) "Afinado a tu semana" else "Tuned to your week").uppercase(),
            style = AtlanType.Label, color = AtlanPalette.TideDeep
        )
        Text(
            copy(LocalizedStringKey.TUNED_TITLE),
            modifier = Modifier.padding(top = AtlanSpacing.sm.dp),
            style = AtlanType.Display, color = AtlanPalette.Abyss
        )

        Column(
            modifier = Modifier.padding(top = AtlanSpacing.xl.dp),
            verticalArrangement = Arrangement.spacedBy(AtlanSpacing.md.dp)
        ) {
            rows.forEach { row ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(AtlanRadii.lg.dp))
                        .background(AtlanPalette.Paper)
                        .padding(AtlanSpacing.lg.dp)
                ) {
                    Text(row.title, style = AtlanType.Body, color = AtlanPalette.Abyss)
                    Text(row.detail, style = AtlanType.Body, color = AtlanPalette.TideDeep)
                }
            }
        }

        Column(Modifier.weight(1f)) {}
        AtlanButton(text = copy(LocalizedStringKey.TUNED_CTA), onClick = onSeeFirstSession, coral = true)
    }
}
