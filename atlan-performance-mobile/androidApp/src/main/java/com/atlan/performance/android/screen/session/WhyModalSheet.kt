package com.atlan.performance.android.screen.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atlan.performance.android.design.AtlanBottomSheet
import com.atlan.performance.android.design.AtlanPalette
import com.atlan.performance.android.design.AtlanType
import com.atlan.performance.shared.AtlanShared
import com.atlan.performance.shared.domain.model.Language
import com.atlan.performance.shared.presentation.WhyModalState

/**
 * Why Modal — native bottom sheet, scrollable, with a close affordance (drag-down / scrim). Tide
 * marks the concept label and mechanism block. No motivational copy inside the science; every
 * mechanism is backed by the reference. Missing content is handled calmly (no error styling).
 */
@Composable
fun WhyModalSheet(
    shared: AtlanShared,
    conceptKey: String,
    language: Language,
    onDismiss: () -> Unit
) {
    val state: WhyModalState by produceState(initialValue = WhyModalState(), conceptKey, language) {
        value = shared.getWhyConcept(conceptKey, language)
    }

    AtlanBottomSheet(onDismiss = onDismiss) {
        val concept = state.concept
        if (concept == null) {
            Text(
                if (language == Language.ES)
                    "Contenido no disponible sin conexión. Volverá cuando se sincronice."
                else
                    "This explanation isn't cached yet. It'll appear once synced.",
                color = AtlanPalette.TideDeep
            )
            return@AtlanBottomSheet
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 560.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(concept.eyebrow.uppercase(), style = AtlanType.Label, color = AtlanPalette.Tide)
            Text(concept.title, style = AtlanType.Display, color = AtlanPalette.Abyss)
            Text(concept.body, style = AtlanType.Body, color = AtlanPalette.Abyss)

            Text(
                if (language == Language.ES) "Mecanismo" else "Mechanism",
                style = AtlanType.Label, color = AtlanPalette.Tide
            )
            concept.mechanisms.forEach { mechanism ->
                Text("• $mechanism", color = AtlanPalette.TideDeep)
            }

            Text(
                if (language == Language.ES) "Referencia" else "Reference",
                style = AtlanType.Label, color = AtlanPalette.Tide
            )
            Text(concept.reference, color = AtlanPalette.Abyss, fontSize = 13.sp)
        }
    }
}
