package com.atlan.performance.android.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * AtlanTheme wraps Material 3 only as a low-level base. The Atlan visual language (calm light
 * surfaces, rare Coral, Tide for science) is expressed through AtlanPalette + AtlanType and the
 * custom AtlanComponents — not Material's default look.
 */
private val AtlanLightColors = lightColorScheme(
    primary = AtlanPalette.Tide,
    onPrimary = AtlanPalette.Foam,
    secondary = AtlanPalette.Coral,
    background = AtlanPalette.FoamWarm,
    onBackground = AtlanPalette.Abyss,
    surface = AtlanPalette.Paper,
    onSurface = AtlanPalette.Abyss,
)

@Composable
fun AtlanTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AtlanLightColors,
        typography = AtlanType.material,
        content = content
    )
}
