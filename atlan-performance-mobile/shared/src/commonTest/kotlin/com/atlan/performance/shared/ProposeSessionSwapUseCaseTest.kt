package com.atlan.performance.shared

import com.atlan.performance.shared.data.fake.FakeSessionRepository
import com.atlan.performance.shared.data.fake.FakeSwapProposalRepository
import com.atlan.performance.shared.data.fake.FakeTrainingPlanRepository
import com.atlan.performance.shared.data.seed.SeedTrainingPlan
import com.atlan.performance.shared.domain.usecase.ProposeSessionSwapUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Given a disrupted pool threshold session, when a swap is proposed, then the replacement is not
 * framed as failure, weekly load is still on track, and no shame copy appears in the proposal.
 */
class ProposeSessionSwapUseCaseTest {

    private val shameWords = listOf(
        "missed", "behind", "streak", "fail", "failure", "shame", "broken", "lazy", "skip the gym"
    )

    @Test
    fun swap_is_not_framed_as_failure_and_weekly_load_stays_on_track() = runTest {
        val planRepo = FakeTrainingPlanRepository()
        val sessionRepo = FakeSessionRepository(planRepo)
        val swapRepo = FakeSwapProposalRepository()
        val useCase = ProposeSessionSwapUseCase(sessionRepo, swapRepo)

        val state = useCase(SeedTrainingPlan.todaySession.id)

        assertNotNull(state, "A swap proposal should be produced for a disrupted session.")

        // Weekly load is still on track.
        assertTrue(
            state.proposal.weeklyLoadStatus.lowercase().contains("on track"),
            "Weekly load must remain on track, was: ${state.proposal.weeklyLoadStatus}"
        )

        // No shame copy anywhere in the proposal or its framing.
        val copy = listOf(
            state.tag,
            state.empathyLine,
            state.originalLabel,
            state.proposal.replacementTitle,
            state.proposal.replacementDetail,
            state.proposal.weeklyLoadStatus,
            state.proposal.affirmation
        ).joinToString(" ").lowercase()

        shameWords.forEach { word ->
            assertTrue(!copy.contains(word), "Proposal copy must not contain shame word: '$word'")
        }
    }
}
