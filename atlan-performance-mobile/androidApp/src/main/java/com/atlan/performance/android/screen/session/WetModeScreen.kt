package com.atlan.performance.android.screen.session

import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atlan.performance.android.design.AtlanButton
import com.atlan.performance.android.design.AtlanPalette
import com.atlan.performance.android.design.AtlanPill
import com.atlan.performance.android.design.WetModeActionZone
import com.atlan.performance.shared.AtlanShared
import com.atlan.performance.shared.domain.model.Language
import com.atlan.performance.shared.domain.model.OfflineStatus
import com.atlan.performance.shared.localization.AtlanCopy
import com.atlan.performance.shared.localization.LocalizedStringKey
import com.atlan.performance.shared.presentation.SetTimerPhase
import com.atlan.performance.shared.presentation.WorkoutTimerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Wet Mode — full-screen, offline, wet-hands 4-set threshold timer (atlan-4-set-timer spec).
 * AbyssDeep bg, Foam text, CoralBright only for productive actions. Very large type, no precision
 * controls.
 *
 * Drift-free monotonic timing via [WorkoutTimerState] + `SystemClock.elapsedRealtime()`. Flow:
 * Active → (Pause) → Overtime → complete → Rest → Active … → summary. Exiting an active session
 * always asks to confirm (progress is never discarded silently).
 *
 * TODO(production): tune swipe thresholds for waterproof-pouch use; persist timer state across
 * process death + restore on resume; queue completion for real sync. Thresholds are large.
 */
@Composable
fun WetModeScreen(
    shared: AtlanShared,
    language: Language,
    onExit: () -> Unit,
    tutorialSeen: Boolean = false,
    onTutorialSeen: () -> Unit = {},
    hapticsEnabled: Boolean = true,
    keepScreenAwake: Boolean = true,
    restSeconds: Int = 30,
    resume: Boolean = false
) {
    // Copy resolves through the shared bilingual layer so ES is first-class on the wet-hands surface.
    fun copy(key: LocalizedStringKey) = AtlanCopy.get(key, language)
    fun copy(key: LocalizedStringKey, vararg args: String) = AtlanCopy.format(key, language, *args)

    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()

    // Keep the screen on during an active workout if the user prefers it.
    val view = LocalView.current
    DisposableEffect(keepScreenAwake) {
        view.keepScreenOn = keepScreenAwake
        onDispose { view.keepScreenOn = false }
    }
    fun buzz() { if (hapticsEnabled) haptics.performHapticFeedback(HapticFeedbackType.LongPress) }

    var timer by remember { mutableStateOf<WorkoutTimerState?>(null) }
    var now by remember { mutableStateOf(0L) }
    var showEarlyConfirm by remember { mutableStateOf(false) }
    var showExitConfirm by remember { mutableStateOf(false) }
    var showTutorial by remember { mutableStateOf(!tutorialSeen) }

    fun monoNow() = SystemClock.elapsedRealtime()

    LaunchedEffect(Unit) {
        val session = shared.getTodaySession()?.session ?: return@LaunchedEffect
        now = monoNow()
        // Resume from a saved snapshot (after relaunch/recovery) or start fresh.
        val progress = if (resume) shared.loadSessionProgress() else null
        timer = if (progress != null && progress.sessionId == session.id) {
            shared.resumeWorkoutTimer(session, progress).started(now)
        } else {
            shared.startWorkoutTimer(session, 1, 105_000L, restSeconds * 1000L, OfflineStatus.OFFLINE_USABLE).started(now)
        }
    }

    // Persist progress at set granularity (survives process death); clear once the session finishes.
    LaunchedEffect(timer?.setIndex, timer?.completedCount, timer?.isComplete) {
        val t = timer ?: return@LaunchedEffect
        if (t.isComplete) shared.clearSessionProgress(t.sessionId)
        else shared.saveSessionProgress(t, restSeconds)
    }

    // 1s monotonic ticker — ACTIVE→OVERTIME at zero; REST auto-starts the next set at zero.
    LaunchedEffect(timer != null) {
        while (true) {
            delay(1000)
            now = monoNow()
            timer?.let { if (it.isRunningOrOvertime || it.isResting) timer = it.ticked(now) }
        }
    }

    fun togglePause() {
        val t = timer ?: return
        now = monoNow()
        when {
            t.isPaused -> { timer = t.resumed(now); buzz() }
            t.isRunningOrOvertime -> { timer = t.paused(now); buzz() }
        }
    }

    fun performComplete() {
        val t = timer ?: return
        scope.launch { shared.completeWorkoutSet(t.sessionId, t.currentSetId) } // local-first write
        buzz()
        now = monoNow()
        timer = t.completedSet(now) // → REST (next set) or COMPLETED_SESSION
    }

    fun attemptComplete() {
        val t = timer ?: return
        if (t.isComplete || t.isResting) return
        now = monoNow()
        if (t.requiresEarlyCompleteConfirm(now, 10_000L)) showEarlyConfirm = true else performComplete()
    }

    fun skipRest() {
        val t = timer ?: return
        if (!t.isResting) return
        now = monoNow()
        timer = t.skipRest(now)
        buzz()
    }

    fun primaryForward() {
        val t = timer ?: return
        if (t.isResting) skipRest() else attemptComplete()
    }

    fun requestExit() {
        val t = timer
        if (t == null || t.isComplete) onExit() else showExitConfirm = true
    }

    fun endSession() {
        val t = timer ?: return onExit()
        now = monoNow()
        timer = t.endedSession(now)
    }

    val threshold = 120f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AtlanPalette.AbyssDeep)
            .semantics {
                customActions = listOf(
                    CustomAccessibilityAction(copy(LocalizedStringKey.WET_MODE_ACTION_COMPLETE)) { primaryForward(); true },
                    CustomAccessibilityAction(copy(LocalizedStringKey.WET_MODE_ACTION_PAUSE)) { togglePause(); true },
                    CustomAccessibilityAction(copy(LocalizedStringKey.WET_MODE_ACTION_EXIT)) { requestExit(); true }
                )
            }
            .pointerInput(timer?.setIndex, timer?.phase) {
                var dx = 0f
                var dy = 0f
                detectDragGestures(
                    onDragStart = { dx = 0f; dy = 0f },
                    onDragEnd = {
                        if (showTutorial) return@detectDragGestures // coach mark intercepts gestures
                        when {
                            dy > threshold * 1.5f -> requestExit()
                            dx > threshold -> primaryForward()
                            dx < -threshold -> togglePause()
                        }
                    },
                    onDrag = { _, drag -> dx += drag.x; dy += drag.y }
                )
            }
    ) {
        val t = timer
        when {
            t == null -> Text("…", color = AtlanPalette.TideSoft, modifier = Modifier.align(Alignment.Center))
            t.isComplete -> SessionSummary(t, language, onExit, Modifier.align(Alignment.Center))
            t.isResting -> RestPhase(t, now, language, Modifier.align(Alignment.TopCenter))
            else -> ActiveTimer(t, now, language, Modifier.align(Alignment.TopCenter))
        }

        if (t != null && !t.isComplete) {
            val resting = t.isResting
            ActionZones(
                leftTitle = when {
                    resting -> copy(LocalizedStringKey.WET_MODE_END)
                    t.isPaused -> copy(LocalizedStringKey.WET_MODE_RESUME)
                    else -> copy(LocalizedStringKey.WET_MODE_PAUSE)
                },
                leftLabel = when {
                    resting -> copy(LocalizedStringKey.WET_MODE_END_SESSION)
                    t.isPaused -> copy(LocalizedStringKey.WET_MODE_LABEL_RESUME)
                    else -> copy(LocalizedStringKey.WET_MODE_LABEL_PAUSE)
                },
                onLeft = { if (resting) requestExit() else togglePause() },
                rightTitle = if (resting) copy(LocalizedStringKey.WET_MODE_SKIP_REST) else copy(LocalizedStringKey.WET_MODE_COMPLETE),
                rightLabel = if (resting) copy(LocalizedStringKey.WET_MODE_LABEL_SKIP_REST) else copy(LocalizedStringKey.WET_MODE_LABEL_COMPLETE),
                onRight = { if (resting) skipRest() else attemptComplete() },
                leftHint = copy(LocalizedStringKey.WET_MODE_SWIPE_LEFT_HINT),
                rightHint = copy(LocalizedStringKey.WET_MODE_SWIPE_RIGHT_HINT),
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        if (showTutorial) {
            TutorialOverlay(language = language, onDismiss = { showTutorial = false; onTutorialSeen() })
        }
    }

    if (showEarlyConfirm && timer != null) {
        val t = timer!!
        AlertDialog(
            onDismissRequest = { showEarlyConfirm = false },
            containerColor = AtlanPalette.Paper,
            title = { Text(copy(LocalizedStringKey.WET_MODE_EARLY_TITLE), color = AtlanPalette.Abyss) },
            text = {
                Text(copy(LocalizedStringKey.WET_MODE_EARLY_MESSAGE, t.setLabel, t.timerLabel(now)),
                    color = AtlanPalette.TideDeep)
            },
            confirmButton = {
                TextButton(onClick = { showEarlyConfirm = false; performComplete() }) {
                    Text(copy(LocalizedStringKey.WET_MODE_EARLY_CONFIRM), color = AtlanPalette.CoralDeep)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEarlyConfirm = false }) {
                    Text(copy(LocalizedStringKey.WET_MODE_KEEP_GOING), color = AtlanPalette.TideDeep)
                }
            }
        )
    }

    if (showExitConfirm) {
        AlertDialog(
            onDismissRequest = { showExitConfirm = false },
            containerColor = AtlanPalette.Paper,
            title = { Text(copy(LocalizedStringKey.WET_MODE_EXIT_TITLE), color = AtlanPalette.Abyss) },
            text = {
                Text(copy(LocalizedStringKey.WET_MODE_EXIT_MESSAGE),
                    color = AtlanPalette.TideDeep)
            },
            confirmButton = {
                TextButton(onClick = { showExitConfirm = false; endSession() }) {
                    Text(copy(LocalizedStringKey.WET_MODE_END_SESSION), color = AtlanPalette.CoralDeep)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirm = false }) {
                    Text(copy(LocalizedStringKey.WET_MODE_KEEP_GOING), color = AtlanPalette.TideDeep)
                }
            }
        )
    }
}

private fun offlineLabel(status: OfflineStatus, language: Language): String {
    val key = when (status) {
        OfflineStatus.ONLINE -> LocalizedStringKey.WET_MODE_OFFLINE_ONLINE
        OfflineStatus.SYNC_PENDING -> LocalizedStringKey.WET_MODE_OFFLINE_SYNC_PENDING
        OfflineStatus.SYNC_FAILED_SAVED_LOCALLY -> LocalizedStringKey.WET_MODE_OFFLINE_SAVED_LOCALLY
        else -> LocalizedStringKey.WET_MODE_OFFLINE_CACHED
    }
    return AtlanCopy.get(key, language)
}

@Composable
private fun ActiveTimer(t: WorkoutTimerState, now: Long, language: Language, modifier: Modifier = Modifier) {
    val overtime = t.phase == SetTimerPhase.OVERTIME
    Column(
        modifier = modifier.fillMaxWidth().padding(top = 56.dp).padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(offlineLabel(t.offlineStatus, language), color = AtlanPalette.TideSoft, fontSize = 14.sp)
        Text(t.setLabel, color = AtlanPalette.Foam, fontSize = 18.sp)
        Text(t.mainMetric, color = AtlanPalette.Foam, fontSize = 56.sp, modifier = Modifier.padding(top = 12.dp))
        Text(t.intensityLabel, color = AtlanPalette.TideSoft, fontSize = 20.sp)
        Text(
            t.timerLabel(now),
            color = if (overtime) AtlanPalette.CoralBright else AtlanPalette.Foam,
            fontSize = 72.sp
        )
        t.targetPaceLabel?.let {
            Text(
                if (overtime) AtlanCopy.get(LocalizedStringKey.WET_MODE_OVER_TARGET_PACE, language)
                else AtlanCopy.format(LocalizedStringKey.WET_MODE_TARGET_PACE, language, it),
                color = if (overtime) AtlanPalette.CoralBright else AtlanPalette.TideSoft)
        }
        when {
            t.isPaused -> Text(AtlanCopy.get(LocalizedStringKey.WET_MODE_PAUSED, language),
                color = AtlanPalette.CoralBright, fontSize = 18.sp)
            overtime -> Text(AtlanCopy.get(LocalizedStringKey.WET_MODE_OVER_TARGET, language),
                color = AtlanPalette.CoralBright)
        }
    }
}

@Composable
private fun RestPhase(t: WorkoutTimerState, now: Long, language: Language, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(top = 56.dp).padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(offlineLabel(t.offlineStatus, language), color = AtlanPalette.TideSoft, fontSize = 14.sp)
        Text(AtlanCopy.format(LocalizedStringKey.WET_MODE_SET_COMPLETE, language, t.justCompletedSetNumber.toString()),
            color = AtlanPalette.Foam, fontSize = 18.sp)
        Text(AtlanCopy.get(LocalizedStringKey.WET_MODE_REST, language),
            color = AtlanPalette.TideSoft, fontSize = 20.sp, modifier = Modifier.padding(top = 12.dp))
        Text(t.timerLabel(now), color = AtlanPalette.Foam, fontSize = 72.sp)
        Text("${AtlanCopy.get(LocalizedStringKey.WET_MODE_NEXT, language)} · ${t.setLabel} · ${t.mainMetric}",
            color = AtlanPalette.TideSoft)
    }
}

@Composable
private fun ActionZones(
    leftTitle: String,
    leftLabel: String,
    onLeft: () -> Unit,
    rightTitle: String,
    rightLabel: String,
    onRight: () -> Unit,
    leftHint: String,
    rightHint: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth().fillMaxHeight(0.32f)) {
        WetModeActionZone(
            title = leftTitle, hint = leftHint, accessibleLabel = leftLabel,
            productive = false, onActivate = onLeft, modifier = Modifier.weight(1f).fillMaxHeight()
        )
        WetModeActionZone(
            title = rightTitle, hint = rightHint, accessibleLabel = rightLabel,
            productive = true, onActivate = onRight, modifier = Modifier.weight(1f).fillMaxHeight()
        )
    }
}

/** One-time gesture coach mark, shown over the timer on first entry. */
@Composable
private fun TutorialOverlay(language: Language, onDismiss: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AtlanPalette.AbyssDeep.copy(alpha = 0.92f))
            .clickable(enabled = false) {} // block taps from reaching the timer beneath
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(AtlanCopy.get(LocalizedStringKey.WET_MODE_TUTORIAL_TITLE, language),
            color = AtlanPalette.Foam, fontSize = 26.sp)
        TutorialRow("→", AtlanCopy.get(LocalizedStringKey.WET_MODE_TUTORIAL_COMPLETE, language))
        TutorialRow("←", AtlanCopy.get(LocalizedStringKey.WET_MODE_TUTORIAL_PAUSE, language))
        TutorialRow("↓", AtlanCopy.get(LocalizedStringKey.WET_MODE_TUTORIAL_EXIT, language))
        AtlanButton(text = AtlanCopy.get(LocalizedStringKey.WET_MODE_TUTORIAL_GOT_IT, language),
            onClick = onDismiss, coral = true, modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
private fun TutorialRow(glyph: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(glyph, color = AtlanPalette.CoralBright, fontSize = 22.sp, modifier = Modifier.padding(end = 16.dp))
        Text(text, color = AtlanPalette.Foam, fontSize = 16.sp)
    }
}

@Composable
private fun SessionSummary(t: WorkoutTimerState, language: Language, onExit: () -> Unit, modifier: Modifier = Modifier) {
    val full = t.completedCount >= t.setCount
    val es = language == Language.ES
    val scope = rememberCoroutineScope()
    var syncState by remember { mutableStateOf(OfflineStatus.SYNC_PENDING) }
    var syncing by remember { mutableStateOf(false) }
    // Optional post-session reflection (perceived effort). Local-only; TODO(persist) with results.
    var effort by remember { mutableStateOf<Int?>(null) }
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            AtlanCopy.get(
                if (full) LocalizedStringKey.WET_MODE_SUMMARY_COMPLETE else LocalizedStringKey.WET_MODE_SUMMARY_ENDED,
                language
            ),
            color = AtlanPalette.Foam, fontSize = 30.sp
        )
        Text(
            AtlanCopy.format(LocalizedStringKey.WET_MODE_SUMMARY_SETS, language,
                t.completedCount.toString(), t.setCount.toString()) + " · ${t.totalElapsedLabel()}",
            color = AtlanPalette.TideSoft, fontSize = 18.sp
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AtlanPalette.Abyss)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            t.sets.forEachIndexed { i, set ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(AtlanCopy.format(LocalizedStringKey.WET_MODE_SET, language, (i + 1).toString()),
                        color = AtlanPalette.Foam, modifier = Modifier.weight(1f))
                    val detail = set.targetPaceLabel?.let { "${set.mainMetric} · $it" } ?: set.mainMetric
                    Text(detail, color = AtlanPalette.TideSoft)
                    Text(
                        if (i < t.completedCount) "  ✓" else "  ○",
                        color = if (i < t.completedCount) AtlanPalette.Tide else AtlanPalette.TideSoft
                    )
                }
            }
        }

        // Optional reflection — perceived effort. No keyboard; calm, never scored or judged.
        Text(if (es) "¿Cómo se sintió?" else "How did that feel?",
            color = AtlanPalette.TideSoft, fontSize = 13.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val labels = if (es) listOf("Fácil", "Moderado", "Duro") else listOf("Easy", "Moderate", "Hard")
            labels.forEachIndexed { i, label ->
                EffortChip(label = label, selected = effort == i) { effort = if (effort == i) null else i }
            }
        }

        // Offline-resilience surfacing — calm, never red; data is always safe locally.
        // TODO(sync): drive a real sync-queue drain. Offline in this build → stays safely local.
        if (syncing) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(color = AtlanPalette.TideSoft, strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp))
                Text("  " + AtlanCopy.get(LocalizedStringKey.WET_MODE_SYNCING, language), color = AtlanPalette.TideSoft)
            }
        } else {
            val failed = syncState == OfflineStatus.SYNC_FAILED_SAVED_LOCALLY
            AtlanPill(AtlanCopy.get(
                if (failed) LocalizedStringKey.WET_MODE_OFFLINE_SAVED_LOCALLY else LocalizedStringKey.WET_MODE_OFFLINE_SYNC_PENDING,
                language
            ))
            Text(
                AtlanCopy.get(
                    if (failed) LocalizedStringKey.WET_MODE_SYNC_FAILED else LocalizedStringKey.WET_MODE_SYNC_SAVED_OFFLINE,
                    language
                ),
                color = AtlanPalette.TideSoft, textAlign = TextAlign.Center, fontSize = 13.sp
            )
            TextButton(onClick = {
                syncing = true
                scope.launch { delay(1200); syncing = false; syncState = OfflineStatus.SYNC_FAILED_SAVED_LOCALLY }
            }) { Text(AtlanCopy.get(LocalizedStringKey.WET_MODE_RETRY_SYNC, language), color = AtlanPalette.TideSoft) }
        }
        AtlanButton(text = AtlanCopy.get(LocalizedStringKey.WET_MODE_DONE, language),
            onClick = onExit, coral = true, modifier = Modifier.padding(top = 12.dp))
    }
}

/** A calm perceived-effort chip. Selection shows a ✓ + Tide fill (color is never the only cue). */
@Composable
private fun EffortChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = if (selected) "✓ $label" else label,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .background(if (selected) AtlanPalette.Tide else AtlanPalette.Abyss)
            .heightIn(min = 40.dp)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        color = if (selected) AtlanPalette.Foam else AtlanPalette.TideSoft
    )
}
