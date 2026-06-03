package com.atlan.performance.android.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atlan.performance.android.design.AtlanBackButton
import com.atlan.performance.android.design.AtlanButton
import com.atlan.performance.android.design.AtlanPalette
import com.atlan.performance.android.design.AtlanPill
import com.atlan.performance.android.design.AtlanSelectableRow
import com.atlan.performance.android.design.AtlanType
import com.atlan.performance.shared.design.AtlanSpacing
import com.atlan.performance.shared.domain.model.Language
import com.atlan.performance.shared.localization.AtlanCopy
import com.atlan.performance.shared.localization.LocalizedStringKey

/**
 * Calibration — tunes coaching posture without scoring or judging. Selected option uses TidePale +
 * Tide. No "good/bad athlete" framing. Answers become settings, not scores. One example question is
 * scaffolded for the initial build; the four-question set is documented in the brief.
 */
@Composable
fun CalibrationScreen(language: Language, onBack: () -> Unit, onContinue: () -> Unit) {
    fun copy(key: LocalizedStringKey) = AtlanCopy.get(key, language)

    val question = if (language == Language.ES)
        "¿Qué te lleva a entrenar la mayoría de las semanas?" else
        "What pulls you to training most weeks?"
    val options = if (language == Language.ES) listOf(
        "El espacio que me da del trabajo",
        "La búsqueda de mejorar",
        "La estructura para mi semana"
    ) else listOf(
        "The space it gives me from work",
        "The pursuit of getting better",
        "The structure for my week"
    )

    var selected by remember { mutableStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AtlanPalette.FoamWarm)
            .padding(AtlanSpacing.xl.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AtlanBackButton(onClick = onBack, contentDescription = if (language == Language.ES) "Atrás" else "Back")
            Column(Modifier.weight(1f)) {}
            AtlanPill("2 of 4")
        }

        Text(
            copy(LocalizedStringKey.CALIBRATION_SUBTITLE),
            modifier = Modifier.padding(top = AtlanSpacing.xl.dp),
            style = AtlanType.Label, color = AtlanPalette.TideDeep
        )
        Text(
            copy(LocalizedStringKey.CALIBRATION_TITLE),
            modifier = Modifier.padding(top = AtlanSpacing.sm.dp),
            style = AtlanType.Display, color = AtlanPalette.Abyss
        )

        Column(
            modifier = Modifier.padding(top = AtlanSpacing.xl.dp),
            verticalArrangement = Arrangement.spacedBy(AtlanSpacing.md.dp)
        ) {
            Text(question, style = AtlanType.Body, color = AtlanPalette.Abyss, fontSize = 18.sp)
            options.forEachIndexed { index, option ->
                AtlanSelectableRow(
                    title = option,
                    isSelected = index == selected,
                    onClick = { selected = index }
                )
            }
        }

        Column(Modifier.weight(1f)) {}
        AtlanButton(
            text = if (language == Language.ES) "Continuar" else "Continue",
            onClick = onContinue
        )
    }
}
