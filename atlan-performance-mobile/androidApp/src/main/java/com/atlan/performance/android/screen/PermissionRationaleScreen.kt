package com.atlan.performance.android.screen

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.atlan.performance.android.design.AtlanBackButton
import com.atlan.performance.android.design.AtlanButton
import com.atlan.performance.android.design.AtlanPalette
import com.atlan.performance.android.design.AtlanType
import com.atlan.performance.android.work.Reminders
import com.atlan.performance.shared.domain.model.Language

/** Which optional capability a rationale screen explains. */
enum class PermissionKind { NOTIFICATIONS, HEALTH }

/**
 * Permission rationale (inventory §5.2) — explains the *value* of an optional capability **before**
 * any system prompt, and never triggers one cold. This milestone has no notification/health infra,
 * so opting in only records intent locally and shows a calm acknowledgement; the real OS prompt +
 * wiring (UserNotifications/WorkManager, HealthKit/Health Connect) is a TODO. Always skippable.
 */
@Composable
fun PermissionRationaleScreen(
    kind: PermissionKind,
    language: Language,
    onBack: () -> Unit
) {
    val es = language == Language.ES
    val context = LocalContext.current
    var acknowledged by remember { mutableStateOf(false) }
    var enabledNow by remember { mutableStateOf(false) }

    // Notifications: request POST_NOTIFICATIONS (API 33+), then schedule the daily reminder on grant.
    val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) { Reminders.scheduleDaily(context); enabledNow = true }
        acknowledged = true
    }
    fun enable() {
        when (kind) {
            PermissionKind.NOTIFICATIONS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !Reminders.notificationsAllowed(context)) {
                    notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    Reminders.scheduleDaily(context); enabledNow = true; acknowledged = true
                }
            }
            PermissionKind.HEALTH -> acknowledged = true
        }
    }

    val title: String
    val body: String
    val points: List<String>
    val cta: String
    when (kind) {
        PermissionKind.NOTIFICATIONS -> {
            title = if (es) "Recordatorios suaves" else "Gentle reminders"
            body = if (es)
                "Atlan puede avisarte antes de una sesión y cuando termina el descanso — con calma, nunca insistiendo."
            else
                "Atlan can nudge you before a session and when rest ends — calm, never nagging."
            points = if (es) listOf(
                "Aviso antes de la sesión de hoy",
                "Señal cuando termina el descanso",
                "Tú eliges la frecuencia · mínima por defecto"
            ) else listOf(
                "A heads-up before today's session",
                "A cue when rest ends",
                "You choose the cadence · minimal by default"
            )
            cta = if (es) "Activar recordatorios" else "Turn on reminders"
        }
        PermissionKind.HEALTH -> {
            title = if (es) "Conecta tus datos de salud" else "Connect your health data"
            body = if (es)
                "Opcional: sincroniza tus sesiones con Apple Health o Health Connect para que tu entrenamiento viva junto al resto de tu salud."
            else
                "Optional: sync your sessions with Apple Health or Health Connect so training lives alongside the rest of your health."
            points = if (es) listOf(
                "Tus entrenos completados, en tu app de salud",
                "Tú tienes el control · nada se comparte sin permiso",
                "Puedes desconectarlo cuando quieras"
            ) else listOf(
                "Your completed workouts, in your health app",
                "You stay in control · nothing shared without permission",
                "Disconnect anytime"
            )
            cta = if (es) "Conectar" else "Connect"
        }
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
        Text((if (es) "Opcional" else "Optional").uppercase(),
            style = AtlanType.Label, color = AtlanPalette.TideDeep)
        Text(title, style = AtlanType.Display, color = AtlanPalette.Abyss)
        Text(body, color = AtlanPalette.Abyss)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AtlanPalette.Paper)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            points.forEach { point ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("·", color = AtlanPalette.Tide)
                    Text(point, color = AtlanPalette.Abyss, modifier = Modifier.weight(1f))
                }
            }
        }

        Column(Modifier.weight(1f)) {}

        if (acknowledged) {
            val confirm = when {
                kind == PermissionKind.NOTIFICATIONS && enabledNow ->
                    if (es) "Recordatorios activados. Te avisaremos antes de las sesiones — con calma."
                    else "Reminders on. We'll nudge you before sessions — calm, never nagging."
                kind == PermissionKind.NOTIFICATIONS ->
                    if (es) "Sin problema. Puedes activarlos cuando quieras en los ajustes del sistema."
                    else "No problem. You can turn reminders on anytime in system settings."
                else ->
                    if (es) "Listo. Te pediremos confirmar el permiso cuando esté disponible."
                    else "Got it. We'll ask you to confirm the permission when it's available."
            }
            Text(confirm, color = AtlanPalette.TideDeep)
            AtlanButton(text = if (es) "Hecho" else "Done", onClick = onBack)
        } else {
            AtlanButton(text = cta, onClick = { enable() }, coral = true)
            Text(
                if (es) "Ahora no" else "Not now",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(999.dp))
                    .clickable(onClick = onBack)
                    .padding(12.dp),
                color = AtlanPalette.TideDeep,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
