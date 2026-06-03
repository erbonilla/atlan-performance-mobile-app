package com.atlan.performance.android.design

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atlan.performance.shared.design.AtlanRadii
import com.atlan.performance.shared.design.AtlanSpacing
import com.atlan.performance.shared.presentation.MetricChip

/**
 * Atlan custom component library. Built on Compose primitives, not Material's default visual
 * language. No component introduces streak / rank / flame / trophy / badge pressure.
 *
 * Pattern coverage (atlan-mobile-design-patterns.md): every interactive control exposes a pressed
 * state (§9.2), meets the 48dp minimum touch target (§11.2), and never signals state through colour
 * alone (§11.5). Async actions get explicit loading + disabled states (§8.1, §10.5–§10.6).
 */

/** Minimum touch target for any tappable control (§11.2 — Android 48dp). */
private val AtlanMinTouchTarget = 48.dp

/** Primary CTA. Dark Abyss pill by default; Coral only where productive action warrants it. */
@Composable
fun AtlanButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    coral: Boolean = false,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    val active = enabled && !loading
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed && active) 0.98f else 1f, label = "atlanButtonPress")
    val bg = when {
        !enabled -> AtlanPalette.TideSoft
        coral -> AtlanPalette.CoralBright
        else -> AtlanPalette.Abyss
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(AtlanRadii.pill.dp))
            .background(bg)
            .heightIn(min = AtlanMinTouchTarget)
            .alpha(if (pressed && active) 0.82f else 1f)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = active,
                onClick = onClick
            )
            .semantics { contentDescription = text; role = Role.Button }
            .padding(vertical = AtlanSpacing.md.dp, horizontal = AtlanSpacing.xl.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (loading) {
            // Preserve height; never let layout shift while async work runs (§9.4).
            CircularProgressIndicator(
                color = AtlanPalette.Foam,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(text, color = AtlanPalette.Foam, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
    }
}

/**
 * Back affordance for pushed screens. Replaces a bare "←" glyph so the tap target meets the 48dp
 * minimum (§11.2) and carries a localized content description + button role for TalkBack (§11.4).
 */
@Composable
fun AtlanBackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "Back"
) {
    Box(
        modifier = modifier
            .size(AtlanMinTouchTarget)
            .clip(RoundedCornerShape(AtlanRadii.pill.dp))
            .clickable(onClick = onClick)
            .semantics { this.contentDescription = contentDescription; role = Role.Button },
        contentAlignment = Alignment.CenterStart
    ) {
        Text("‹", color = AtlanPalette.Abyss, fontSize = 30.sp, fontWeight = FontWeight.Bold)
    }
}

/** Small status/label pill (e.g. progress "2 of 4", "Offline · Cached"). */
@Composable
fun AtlanPill(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(AtlanRadii.pill.dp))
            .background(AtlanPalette.TidePale)
            .padding(horizontal = AtlanSpacing.md.dp, vertical = AtlanSpacing.xs.dp)
    ) {
        Text(text, color = AtlanPalette.TideDeep, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

/**
 * Tide-outlined circular "Why" affordance with an italic i. Tide — never Coral. The visible ring
 * stays 24dp for calm density, but the tap target expands to the 48dp minimum (§11.2).
 */
@Composable
fun AtlanInfoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accessibleLabel: String = "Why — open explanation"
) {
    Box(
        modifier = modifier
            .size(AtlanMinTouchTarget)
            .clip(RoundedCornerShape(AtlanRadii.pill.dp))
            .clickable(onClick = onClick)
            .semantics { contentDescription = accessibleLabel; role = Role.Button },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(AtlanRadii.pill.dp))
                .border(1.5.dp, AtlanPalette.Tide, RoundedCornerShape(AtlanRadii.pill.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("i", color = AtlanPalette.Tide, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * Single-select option row (Calibration, language-style choices). Selection is signalled by a
 * check glyph + border + fill, never colour alone (§11.5), and exposes the selected semantics state
 * so TalkBack announces it (§11.4). Whole row is tappable and meets the 48dp minimum (§8.2, §11.2).
 */
@Composable
fun AtlanSelectableRow(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) AtlanPalette.Tide else AtlanPalette.TideSoft
    val bg = if (isSelected) AtlanPalette.TidePale else AtlanPalette.Paper
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AtlanMinTouchTarget)
            .clip(RoundedCornerShape(AtlanRadii.lg.dp))
            .background(bg)
            .border(if (isSelected) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(AtlanRadii.lg.dp))
            .clickable(onClick = onClick)
            .semantics { selected = isSelected; role = Role.RadioButton }
            .padding(AtlanSpacing.lg.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            color = if (isSelected) AtlanPalette.TideDeep else AtlanPalette.Abyss,
            style = AtlanType.Body,
            modifier = Modifier.weight(1f)
        )
        Text(
            if (isSelected) "✓" else "○",
            color = if (isSelected) AtlanPalette.Tide else AtlanPalette.TideSoft,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

/** Dashboard metric chip with inline Why affordance. */
@Composable
fun AtlanMetricChip(chip: MetricChip, onWhy: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(AtlanRadii.lg.dp))
            .background(AtlanPalette.Paper)
            .padding(AtlanSpacing.lg.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(chip.title.uppercase(), style = AtlanType.Label, color = AtlanPalette.TideDeep)
            chip.whyConceptKey?.let {
                AtlanInfoButton(
                    onClick = { onWhy(it) },
                    accessibleLabel = "Why ${chip.title} — open explanation"
                )
            }
        }
        Text(chip.value, style = AtlanType.Numeric, color = AtlanPalette.Abyss)
        Text(chip.detail, style = AtlanType.Body, color = AtlanPalette.Abyss)
    }
}

/**
 * Reusable calm error surface (§10.8). No blame, no red — Tide retry glyph, explains the issue and
 * reassures that data is safe, offers Retry (and an optional safe exit). Used as a load-failure
 * fallback so unrecoverable states never strand the user.
 */
@Composable
fun AtlanErrorScreen(
    title: String,
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    retryText: String = "Retry",
    exitText: String? = null,
    onExit: (() -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(AtlanSpacing.xl.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AtlanSpacing.md.dp)
    ) {
        Text("↻", color = AtlanPalette.TideDeep, fontSize = 44.sp)
        Text(title, style = AtlanType.Display, color = AtlanPalette.Abyss, textAlign = TextAlign.Center)
        Text(message, color = AtlanPalette.TideDeep, textAlign = TextAlign.Center)
        AtlanButton(text = retryText, onClick = onRetry, modifier = Modifier.padding(top = AtlanSpacing.sm.dp))
        if (onExit != null && exitText != null) {
            Text(
                exitText,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .clickable(onClick = onExit)
                    .padding(AtlanSpacing.sm.dp),
                color = AtlanPalette.TideDeep,
                textAlign = TextAlign.Center
            )
        }
    }
}

/** Card wrapper for session content. */
@Composable
fun AtlanSessionCard(modifier: Modifier = Modifier, content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AtlanRadii.phone.dp))
            .background(AtlanPalette.Abyss)
            .padding(AtlanSpacing.xl.dp),
        verticalArrangement = Arrangement.spacedBy(AtlanSpacing.sm.dp),
        content = content
    )
}

/** Native bottom sheet wrapper for Why Modal and Session Swapper. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtlanBottomSheet(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AtlanPalette.Paper,
        shape = RoundedCornerShape(topStart = AtlanRadii.sheet.dp, topEnd = AtlanRadii.sheet.dp)
    ) {
        Column(Modifier.padding(AtlanSpacing.xl.dp)) { content() }
    }
}

/**
 * One half of the Wet Mode bottom action grid. Large touch zone, no precision tap. Exposes an
 * accessible action so swipe is never the only path. Coral only for the Complete (productive) zone.
 */
@Composable
fun WetModeActionZone(
    title: String,
    hint: String,
    accessibleLabel: String,
    productive: Boolean,
    onActivate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (productive) AtlanPalette.CoralBright else AtlanPalette.Abyss
    Column(
        modifier = modifier
            .background(bg)
            .clickable(onClick = onActivate)
            .semantics { contentDescription = accessibleLabel; role = Role.Button }
            .padding(AtlanSpacing.xl.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(title, color = AtlanPalette.Foam, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(hint, color = AtlanPalette.Foam, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}
