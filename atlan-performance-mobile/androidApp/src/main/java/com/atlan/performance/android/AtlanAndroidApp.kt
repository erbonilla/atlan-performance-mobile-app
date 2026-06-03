package com.atlan.performance.android

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.atlan.performance.android.design.AtlanTheme
import com.atlan.performance.android.navigation.AtlanNavGraph
import com.atlan.performance.android.screen.AtlanSplash
import com.atlan.performance.shared.AtlanShared

/**
 * Root composable. Creates the shared core once (fake repositories are seeded on construction —
 * the offline-first source of truth) and hosts the navigation graph inside AtlanTheme, after a brief
 * branded launch surface.
 */
@Composable
fun AtlanAndroidApp() {
    val shared = remember { AtlanShared() }
    var splashDone by remember { mutableStateOf(false) }
    AtlanTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
            // Screens manage their own backgrounds; Scaffold provides safe-area insets.
            androidx.compose.foundation.layout.Box(Modifier.padding(padding)) {
                if (splashDone) AtlanNavGraph(shared) else AtlanSplash(onDone = { splashDone = true })
            }
        }
    }
}
