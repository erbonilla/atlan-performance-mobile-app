package com.atlan.performance.android.design

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.sp

/**
 * Atlan typography. Sizes are scalable (sp) for font-scaling support. Custom fonts are stubbed:
 * swap `FontFamily.Default` for bundled Fraunces (display) / Manrope (body) resources later.
 * Spanish strings wrap; they are never shrunk to fit.
 */
object AtlanType {

    // TODO: replace with bundled FontFamily(Font(R.font.fraunces)) when assets are installed.
    private val DisplayFamily = FontFamily.Default
    private val BodyFamily = FontFamily.Default

    val Display = TextStyle(
        fontFamily = DisplayFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        lineHeight = 36.sp
    )

    val Label = TextStyle(
        fontFamily = BodyFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 1.2.sp
    )

    val Body = TextStyle(
        fontFamily = BodyFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    )

    /** Tabular numeric where available; used for distance/pace numerics. */
    val Numeric = TextStyle(
        fontFamily = BodyFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        textMotion = TextMotion.Static
    )

    /** Extra-large, high-contrast Wet Mode numbers. */
    val WetMode = TextStyle(
        fontFamily = DisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 72.sp,
        lineHeight = 76.sp
    )

    val material = Typography(
        displaySmall = Display,
        bodyLarge = Body,
        labelSmall = Label
    )
}
