package com.atlan.performance.shared.domain.usecase

import com.atlan.performance.shared.domain.model.OfflineStatus
import com.atlan.performance.shared.domain.repository.SessionRepository
import com.atlan.performance.shared.domain.repository.TrainingPlanRepository
import com.atlan.performance.shared.presentation.DashboardState
import com.atlan.performance.shared.presentation.MetricChip
import com.atlan.performance.shared.presentation.WeeklyArcState

/**
 * Builds the calm dashboard. It deliberately surfaces only "what is today" and "is the week on
 * track", plus Why-affordance data. It never produces streak, leaderboard, peer-rank, or
 * missed-session fields — DashboardState has no place to put them.
 */
class GetTodayDashboardUseCase(
    private val sessionRepository: SessionRepository,
    private val trainingPlanRepository: TrainingPlanRepository
) {
    suspend operator fun invoke(
        dateLabel: String = "Tuesday · May 26",
        offlineStatus: OfflineStatus = OfflineStatus.OFFLINE_USABLE
    ): DashboardState {
        val today = sessionRepository.getTodaySession()
        val week = trainingPlanRepository.getCurrentWeek()

        val loadStatus = week?.loadStatus ?: "On track"
        val weeklyArc = WeeklyArcState(
            label = "Weekly arc",
            status = "Load ${loadStatus.replaceFirstChar { it.lowercase() }}",
            currentWeekIndex = week?.index ?: 0,
            totalWeeks = week?.totalWeeks ?: 0,
            points = listOf(0.30f, 0.45f, 0.50f, 0.62f, 0.70f, 0.66f),
            currentWeekMarker = 4
        )

        val chips = listOf(
            MetricChip("load", "Load", "On track", "Week ${week?.index ?: 18} of ${week?.totalWeeks ?: 24}", "load"),
            // Consistency is a calm, qualitative pattern — never a consecutive-day streak counter.
            // A breakable day-count would punish the disruption Atlan exists to absorb (principle #1
            // + the hard no-streak rule), so we surface the pattern without a number to "break".
            MetricChip("consistency", "Consistency", "Steady", "Sustainable pattern", "consistency"),
            MetricChip("recovery", "Recovery", "Good", "Sleep 7h32 avg", "recovery")
        )

        return DashboardState(
            dateLabel = dateLabel,
            todaySession = today,
            weeklyArc = weeklyArc,
            metricChips = chips,
            offlineStatus = offlineStatus,
            todayWhyConceptKey = today?.whyConceptKey
        )
    }
}
