package com.atlan.performance.android.screen.settings

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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atlan.performance.android.design.AtlanBackButton
import com.atlan.performance.android.design.AtlanPalette
import com.atlan.performance.android.design.AtlanType
import com.atlan.performance.shared.domain.model.Language

/**
 * Settings — a light control surface. No account system in the first setup. Language switches the
 * whole app live (bilingual is first-class); the Wet Mode preferences (haptics, keep-awake) take
 * effect immediately. Inferred onboarding settings are surfaced so they can become editable later.
 */
@Composable
fun SettingsScreen(
    language: Language,
    onBack: () -> Unit,
    onLanguageChange: (Language) -> Unit,
    hapticsEnabled: Boolean,
    onHapticsChange: (Boolean) -> Unit,
    keepAwake: Boolean,
    onKeepAwakeChange: (Boolean) -> Unit,
    restSeconds: Int,
    onRestSecondsChange: (Int) -> Unit
) {
    val es = language == Language.ES
    val displayRows = if (es) listOf(
        "Cadencia de notificaciones" to "Mínima",
        "Densidad de explicación" to "Estándar",
        "Caché sin conexión" to "Hoy + 7 días",
        "Acerca de Atlan" to "v0.1"
    ) else listOf(
        "Notification cadence" to "Minimal",
        "Explanation density" to "Standard",
        "Offline cache" to "Today + 7 days",
        "About Atlan" to "v0.1"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AtlanPalette.FoamWarm)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AtlanBackButton(onClick = onBack, contentDescription = if (es) "Atrás" else "Back")
        Text(if (es) "Ajustes" else "Settings", style = AtlanType.Display, color = AtlanPalette.Abyss)

        // Language — live, co-equal switch.
        PaperRow {
            Text(if (es) "Idioma" else "Language", color = AtlanPalette.Abyss, modifier = Modifier.weight(1f))
            LanguageSegment(es = es, onChange = onLanguageChange)
        }

        // Wet Mode preferences — take effect immediately.
        ToggleRow(if (es) "Vibración" else "Haptics", hapticsEnabled, onHapticsChange)
        ToggleRow(if (es) "Pantalla siempre encendida" else "Keep screen awake", keepAwake, onKeepAwakeChange)

        // Timer preference — rest window between sets; feeds StartWorkoutTimerUseCase live.
        PaperRow {
            Text(if (es) "Descanso entre series" else "Rest between sets",
                color = AtlanPalette.Abyss, modifier = Modifier.weight(1f))
            RestSegment(restSeconds = restSeconds, onChange = onRestSecondsChange)
        }

        displayRows.forEach { (title, value) ->
            PaperRow {
                Text(title, color = AtlanPalette.Abyss, modifier = Modifier.weight(1f))
                Text(value, color = AtlanPalette.TideDeep)
            }
        }
    }
}

@Composable
private fun PaperRow(content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AtlanPalette.Paper)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun ToggleRow(title: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    PaperRow {
        Text(title, color = AtlanPalette.Abyss, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AtlanPalette.Foam,
                checkedTrackColor = AtlanPalette.Tide,
                uncheckedTrackColor = AtlanPalette.TideSoft
            )
        )
    }
}

@Composable
private fun LanguageSegment(es: Boolean, onChange: (Language) -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(AtlanPalette.TidePale)
    ) {
        Segment("English", selected = !es) { onChange(Language.EN) }
        Segment("Español", selected = es) { onChange(Language.ES) }
    }
}

@Composable
private fun RestSegment(restSeconds: Int, onChange: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(AtlanPalette.TidePale)
    ) {
        listOf(30, 45, 60).forEach { seconds ->
            Segment("${seconds}s", selected = restSeconds == seconds) { onChange(seconds) }
        }
    }
}

@Composable
private fun Segment(title: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        title,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .semantics { this.selected = selected; role = Role.Button }
            .background(if (selected) AtlanPalette.Abyss else androidx.compose.ui.graphics.Color.Transparent)
            .heightIn(min = 36.dp)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        color = if (selected) AtlanPalette.Foam else AtlanPalette.TideDeep,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    )
}
