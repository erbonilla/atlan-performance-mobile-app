package com.atlan.performance.shared

import com.atlan.performance.shared.data.fake.FakeSessionRepository
import com.atlan.performance.shared.data.fake.FakeTrainingPlanRepository
import com.atlan.performance.shared.domain.usecase.GetTodayDashboardUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * The dashboard returns today's session, exposes Why-affordance data and weekly-arc state, and
 * surfaces no streak / leaderboard / rank pressure content.
 *
 * Absence of streak/leaderboard *fields* is guaranteed structurally by DashboardState's definition
 * (a compile-time property — there is nowhere to store them). This test verifies the runtime content
 * carries no such pressure mechanics, using only multiplatform-safe checks (no JVM reflection).
 */
class GetTodayDashboardUseCaseTest {

    private val forbidden = listOf("streak", "leaderboard", "rank", "missed", "badge", "behind")

    @Test
    fun dashboard_has_today_why_and_weekly_arc_but_no_pressure_content() = runTest {
        val planRepo = FakeTrainingPlanRepository()
        val sessionRepo = FakeSessionRepository(planRepo)
        val useCase = GetTodayDashboardUseCase(sessionRepo, planRepo)

        val state = useCase()

        // Returns today's session.
        assertNotNull(state.todaySession, "Dashboard must return today's session.")

        // Contains Why affordance data.
        assertNotNull(state.todayWhyConceptKey, "Dashboard must expose a Why affordance for today.")
        assertTrue(
            state.metricChips.any { it.whyConceptKey != null },
            "Metric chips must expose Why affordances."
        )

        // Contains weekly arc state.
        assertTrue(state.weeklyArc.points.isNotEmpty(), "Dashboard must contain weekly arc state.")
        assertTrue(state.weeklyArc.totalWeeks > 0, "Weekly arc must describe plan progress.")

        // No pressure content in any user-visible dashboard string.
        val visible = buildList {
            add(state.dateLabel)
            add(state.weeklyArc.label)
            add(state.weeklyArc.status)
            state.metricChips.forEach { add(it.title); add(it.value); add(it.detail) }
        }.joinToString(" ").lowercase()

        forbidden.forEach { term ->
            assertTrue(!visible.contains(term), "Dashboard must not surface '$term'.")
        }
    }
}
