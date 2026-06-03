package com.atlan.performance.android.screen.session

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.atlan.performance.android.design.AtlanButton
import com.atlan.performance.android.design.AtlanBottomSheet
import com.atlan.performance.android.design.AtlanPalette
import com.atlan.performance.android.design.AtlanPill
import com.atlan.performance.android.design.AtlanType
import com.atlan.performance.shared.AtlanShared
import com.atlan.performance.shared.presentation.SessionSwapperState
import kotlinx.coroutines.launch

/**
 * Session Swapper — absorbs disruption without shame. Neither action is failure: Accept updates the
 * plan locally and queues sync; Skip updates locally with no red/failure copy. No warning states.
 */
@Composable
fun SessionSwapperSheet(
    shared: AtlanShared,
    sessionId: String,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var accepting by remember { mutableStateOf(false) }
    val state: SessionSwapperState? by produceState<SessionSwapperState?>(initialValue = null, sessionId) {
        value = shared.proposeSessionSwap(sessionId)
    }

    AtlanBottomSheet(onDismiss = onDismiss) {
        val s = state ?: run {
            Text("…", color = AtlanPalette.TideDeep)
            return@AtlanBottomSheet
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AtlanPill(s.tag)
            Text(s.empathyLine, style = AtlanType.Display, color = AtlanPalette.Abyss)

            // Original.
            SwapRow(label = "Original", value = s.originalLabel)
            // Replacement.
            SwapRow(
                label = "Replacement",
                value = "${s.proposal.replacementTitle} · ${s.proposal.replacementDetail}"
            )
            // Weekly load — calm, never red.
            SwapRow(label = "Weekly load", value = s.proposal.weeklyLoadStatus)

            Text(s.proposal.affirmation, color = AtlanPalette.TideDeep, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))

            AtlanButton(
                text = "Accept swap",
                coral = true,
                enabled = !accepting,
                loading = accepting,
                onClick = {
                    accepting = true
                    scope.launch {
                        shared.acceptSessionSwap.accept(s.proposal.id, s.proposal.originalSessionId)
                        onDismiss()
                    }
                }
            )
            Text(
                "Skip today",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .clickable(enabled = !accepting) {
                        scope.launch {
                            shared.acceptSessionSwap.skipToday(sessionId)
                            onDismiss()
                        }
                    }
                    .padding(12.dp),
                color = AtlanPalette.TideDeep,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SwapRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AtlanPalette.FoamWarm)
            .padding(16.dp)
    ) {
        Text(label.uppercase(), style = AtlanType.Label, color = AtlanPalette.TideDeep)
        Text(value, color = AtlanPalette.Abyss)
    }
}
