package com.atlan.performance.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.atlan.performance.android.work.SyncWorker

/**
 * Single-activity host. The app launches directly to Language Selection (the first onboarding step)
 * via AtlanAndroidApp → AtlanNavGraph.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Drain any pending offline writes once connectivity allows (offline-first background worker).
        SyncWorker.schedule(applicationContext)
        setContent {
            AtlanAndroidApp()
        }
    }
}
