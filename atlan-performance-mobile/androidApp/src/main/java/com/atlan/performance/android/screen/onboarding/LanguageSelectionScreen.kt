package com.atlan.performance.android.screen.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atlan.performance.android.R
import com.atlan.performance.android.design.AtlanButton
import com.atlan.performance.android.design.AtlanPalette
import com.atlan.performance.shared.design.AtlanSpacing
import com.atlan.performance.shared.domain.model.Language

/**
 * Language Selection — choose EN or ES before account creation. No default selection, no flag icons,
 * no locale/IP inference. Both prompts shown so neither language is gated behind the other.
 */
@Composable
fun LanguageSelectionScreen(onSelect: (Language) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AtlanPalette.FoamWarm),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(AtlanSpacing.xl.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.atlan_logo),
                contentDescription = "Atlan Performance",
                modifier = Modifier.size(112.dp)
            )

            // Co-equal bilingual heading — equal size/weight so neither reads as secondary (§4.1).
            Text(
                "Choose your language",
                modifier = Modifier.padding(top = AtlanSpacing.xxl.dp),
                color = AtlanPalette.Abyss, fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                "Elige tu idioma",
                modifier = Modifier.padding(top = AtlanSpacing.xs.dp),
                color = AtlanPalette.TideDeep, fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Column(
                modifier = Modifier
                    .padding(top = AtlanSpacing.xxl.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AtlanSpacing.md.dp)
            ) {
                AtlanButton(text = "English", onClick = { onSelect(Language.EN) })
                AtlanButton(text = "Español", onClick = { onSelect(Language.ES) })
            }
        }
    }
}
