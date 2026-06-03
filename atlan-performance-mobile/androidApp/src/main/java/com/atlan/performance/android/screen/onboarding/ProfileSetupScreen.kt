package com.atlan.performance.android.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atlan.performance.android.design.AtlanBackButton
import com.atlan.performance.android.design.AtlanButton
import com.atlan.performance.android.design.AtlanPalette
import com.atlan.performance.android.design.AtlanType
import com.atlan.performance.shared.domain.model.Language

/**
 * Profile Setup — a light, optional onboarding step (inventory §5.2). Captures a name and training
 * level to tune coaching tone; both can be skipped. No scoring, no judgement — these are settings.
 * Stored locally (platform prefs); no account/backend. Bilingual (inline EN/ES — not yet keyed).
 */
@Composable
fun ProfileSetupScreen(
    language: Language,
    initialName: String,
    initialLevel: String,
    onBack: () -> Unit,
    onContinue: (name: String, level: String) -> Unit
) {
    val es = language == Language.ES
    var name by remember { mutableStateOf(initialName) }
    var level by remember { mutableStateOf(initialLevel) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AtlanPalette.FoamWarm)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AtlanBackButton(onClick = onBack, contentDescription = if (es) "Atrás" else "Back")
        Text((if (es) "Sobre ti" else "About you").uppercase(),
            style = AtlanType.Label, color = AtlanPalette.TideDeep)
        Text(if (es) "Ajusta tu entrenamiento" else "Tune your training",
            style = AtlanType.Display, color = AtlanPalette.Abyss)
        Text(
            if (es) "Opcional. Nos ayuda a ajustar el tono — nunca es una nota."
            else "Optional. It helps us tune the tone — never a grade.",
            color = AtlanPalette.TideDeep
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(if (es) "Tu nombre" else "Your name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = AtlanPalette.Paper,
                unfocusedContainerColor = AtlanPalette.Paper,
                focusedIndicatorColor = AtlanPalette.Tide,
                unfocusedIndicatorColor = AtlanPalette.TideSoft,
                focusedLabelColor = AtlanPalette.TideDeep
            )
        )

        Text(if (es) "Nivel de entrenamiento" else "Training level",
            style = AtlanType.Label, color = AtlanPalette.TideDeep, modifier = Modifier.padding(top = 4.dp))
        val levels = listOf(
            "beginner" to (if (es) "Principiante" else "Beginner"),
            "intermediate" to (if (es) "Intermedio" else "Intermediate"),
            "advanced" to (if (es) "Avanzado" else "Advanced")
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            levels.forEach { (key, label) ->
                LevelRow(label = label, selected = level == key) { level = if (level == key) "" else key }
            }
        }

        Column(Modifier.weight(1f)) {}
        AtlanButton(text = if (es) "Continuar" else "Continue",
            onClick = { onContinue(name.trim(), level) })
    }
}

@Composable
private fun LevelRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) AtlanPalette.TidePale else AtlanPalette.Paper)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text((if (selected) "✓ " else "") + label,
            color = if (selected) AtlanPalette.TideDeep else AtlanPalette.Abyss,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 16.sp)
    }
}
