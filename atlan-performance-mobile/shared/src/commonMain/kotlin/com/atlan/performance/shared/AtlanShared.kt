package com.atlan.performance.shared

import com.atlan.performance.shared.data.fake.FakeSessionRepository
import com.atlan.performance.shared.data.fake.FakeSwapProposalRepository
import com.atlan.performance.shared.data.fake.FakeSyncQueueRepository
import com.atlan.performance.shared.data.fake.FakeTrainingPlanRepository
import com.atlan.performance.shared.data.fake.FakeUserProfileRepository
import com.atlan.performance.shared.data.fake.FakeWhyConceptRepository
import com.atlan.performance.shared.domain.usecase.AcceptSessionSwapUseCase
import com.atlan.performance.shared.domain.usecase.CompleteOnboardingUseCase
import com.atlan.performance.shared.domain.usecase.CompleteWorkoutSetUseCase
import com.atlan.performance.shared.domain.usecase.GetTodayDashboardUseCase
import com.atlan.performance.shared.domain.usecase.GetTodaySessionUseCase
import com.atlan.performance.shared.domain.usecase.GetWhyConceptUseCase
import com.atlan.performance.shared.domain.usecase.ProposeSessionSwapUseCase
import com.atlan.performance.shared.domain.usecase.StartWorkoutTimerUseCase

/**
 * Composition root for the shared core.
 *
 * The initial setup wires fake in-memory repositories seeded on construction. The repository
 * interfaces are designed so these fakes can be replaced by real local-database repositories
 * later without changing use cases or platform UI. See OFFLINE_FIRST.md.
 */
class AtlanShared {

    private val userProfileRepository = FakeUserProfileRepository()
    private val trainingPlanRepository = FakeTrainingPlanRepository()
    private val sessionRepository = FakeSessionRepository(trainingPlanRepository)
    private val whyConceptRepository = FakeWhyConceptRepository()
    private val swapProposalRepository = FakeSwapProposalRepository()
    private val syncQueueRepository = FakeSyncQueueRepository()

    val completeOnboarding = CompleteOnboardingUseCase(userProfileRepository)
    val getTodayDashboard = GetTodayDashboardUseCase(sessionRepository, trainingPlanRepository)
    val getTodaySession = GetTodaySessionUseCase(sessionRepository)
    val getWhyConcept = GetWhyConceptUseCase(whyConceptRepository)
    val proposeSessionSwap = ProposeSessionSwapUseCase(sessionRepository, swapProposalRepository)
    val acceptSessionSwap = AcceptSessionSwapUseCase(swapProposalRepository, sessionRepository, syncQueueRepository)
    val completeWorkoutSet = CompleteWorkoutSetUseCase(sessionRepository, syncQueueRepository)
    val startWorkoutTimer = StartWorkoutTimerUseCase()
}
