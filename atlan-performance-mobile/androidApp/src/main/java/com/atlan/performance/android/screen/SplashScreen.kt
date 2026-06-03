package com.atlan.performance.android.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atlan.performance.android.design.AtlanPalette
import com.atlan.performance.shared.design.AtlanSpacing
import kotlinx.coroutines.delay

/**
 * Brief branded launch surface — the Atlan wordmark on the calm Foam brand surface, fading in before
 * the app opens to Language Selection. Native, no artificial blocking: it auto-advances via [onDone].
 * The window background (themes.xml) is also Foam, so the pre-Compose cold-start frame matches.
 */
@Composable
fun AtlanSplash(onDone: () -> Unit) {
    var shown by remember { mutableStateOf(false) }
    val fade by animateFloatAsState(if (shown) 1f else 0f, tween(450), label = "splashFade")

    LaunchedEffect(Unit) {
        shown = true
        delay(800)
        onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AtlanPalette.FoamWarm)
            .semantics { contentDescription = "Atlan Performance" },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.alpha(fade)) {
            Text("atlan", fontSize = 44.sp, fontWeight = FontWeight.SemiBold, color = AtlanPalette.Abyss)
            Box(
                Modifier
                    .padding(top = AtlanSpacing.xs.dp)
                    .width(52.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(AtlanPalette.Coral)
            )
        }
    }
}
