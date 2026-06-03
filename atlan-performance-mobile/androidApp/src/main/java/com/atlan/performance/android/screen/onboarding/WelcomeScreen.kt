package com.atlan.performance.android.screen.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atlan.performance.android.design.AtlanBackButton
import com.atlan.performance.android.design.AtlanButton
import com.atlan.performance.android.design.AtlanPalette
import com.atlan.performance.android.design.AtlanType
import com.atlan.performance.shared.design.AtlanSpacing
import com.atlan.performance.shared.domain.model.Language
import com.atlan.performance.shared.localization.AtlanCopy
import com.atlan.performance.shared.localization.LocalizedStringKey

/**
 * Welcome — frames the app as adaptive and autonomy-supportive. Copy is resolved from the shared
 * bilingual layer; long Spanish strings wrap (no shrink-to-fit).
 */
@Composable
fun WelcomeScreen(language: Language, onBack: () -> Unit, onBegin: () -> Unit) {
    fun copy(key: LocalizedStringKey) = AtlanCopy.get(key, language)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AtlanPalette.FoamWarm)
            .padding(AtlanSpacing.xl.dp)
    ) {
        AtlanBackButton(onClick = onBack, contentDescription = if (language == Language.ES) "Atrás" else "Back")

        Text(
            copy(LocalizedStringKey.ONBOARDING_WELCOME_EYEBROW).uppercase(),
            modifier = Modifier.padding(top = AtlanSpacing.xxl.dp),
            style = AtlanType.Label, color = AtlanPalette.TideDeep
        )
        Text(
            copy(LocalizedStringKey.ONBOARDING_WELCOME_TITLE),
            modifier = Modifier.padding(top = AtlanSpacing.sm.dp),
            style = AtlanType.Display, color = AtlanPalette.Abyss
        )
        Text(
            copy(LocalizedStringKey.ONBOARDING_WELCOME_BODY),
            modifier = Modifier.padding(top = AtlanSpacing.lg.dp),
            style = AtlanType.Body, color = AtlanPalette.Abyss
        )

        Column(Modifier.weight(1f)) {}
        AtlanButton(text = copy(LocalizedStringKey.ONBOARDING_WELCOME_CTA), onClick = onBegin)
    }
}
