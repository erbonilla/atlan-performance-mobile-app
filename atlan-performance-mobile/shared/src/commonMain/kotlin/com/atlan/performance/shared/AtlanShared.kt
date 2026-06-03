package com.atlan.performance.shared

import com.atlan.performance.shared.data.fake.FakeSessionRepository
import com.atlan.performance.shared.data.fake.FakeSwapProposalRepository
import com.atlan.performance.shared.data.fake.FakeTrainingPlanRepository
import com.atlan.performance.shared.data.fake.FakeUserProfileRepository
import com.atlan.performance.shared.data.fake.FakeWhyConceptRepository
import com.atlan.performance.shared.data.local.SqlDelightSessionProgressRepository
import com.atlan.performance.shared.data.local.SqlDelightSyncQueueRepository
import com.atlan.performance.shared.db.DatabaseDriverFactory
import com.atlan.performance.shared.db.createAtlanDatabase
import com.atlan.performance.shared.domain.usecase.AcceptSessionSwapUseCase
import com.atlan.performance.shared.domain.usecase.ClearSessionProgressUseCase
import com.atlan.performance.shared.domain.usecase.CompleteOnboardingUseCase
import com.atlan.performance.shared.domain.usecase.CompleteWorkoutSetUseCase
import com.atlan.performance.shared.domain.usecase.GetTodayDashboardUseCase
import com.atlan.performance.shared.domain.usecase.GetTodaySessionUseCase
import com.atlan.performance.shared.domain.usecase.GetTrainingPlanUseCase
import com.atlan.performance.shared.domain.usecase.GetWhyConceptUseCase
import com.atlan.performance.shared.domain.usecase.LoadSessionProgressUseCase
import com.atlan.performance.shared.domain.usecase.ProposeSessionSwapUseCase
import com.atlan.performance.shared.domain.usecase.ResumeWorkoutTimerUseCase
import com.atlan.performance.shared.domain.usecase.SaveSessionProgressUseCase
import com.atlan.performance.shared.domain.usecase.StartWorkoutTimerUseCase

/**
 * Composition root for the shared core.
 *
 * Repositories that are still fakes (profile/plan/session/why/swap) remain in-memory and seeded on
 * construction. The **sync queue is now backed by a real local SQLDelight database** — the first of
 * the fakes to be replaced by durable storage (the platform supplies the driver). The remaining fakes
 * follow the same swap pattern. See OFFLINE_FIRST.md.
 */
class AtlanShared(databaseDriverFactory: DatabaseDriverFactory) {

    private val database = createAtlanDatabase(databaseDriverFactory)

    private val userProfileRepository = FakeUserProfileRepository()
    private val trainingPlanRepository = FakeTrainingPlanRepository()
    private val sessionRepository = FakeSessionRepository(trainingPlanRepository)
    private val whyConceptRepository = FakeWhyConceptRepository()
    private val swapProposalRepository = FakeSwapProposalRepository()
    private val syncQueueRepository = SqlDelightSyncQueueRepository(database)
    private val sessionProgressRepository = SqlDelightSessionProgressRepository(database)

    val completeOnboarding = CompleteOnboardingUseCase(userProfileRepository)
    val getTodayDashboard = GetTodayDashboardUseCase(sessionRepository, trainingPlanRepository)
    val getTodaySession = GetTodaySessionUseCase(sessionRepository)
    val getTrainingPlan = GetTrainingPlanUseCase(trainingPlanRepository)
    val getWhyConcept = GetWhyConceptUseCase(whyConceptRepository)
    val proposeSessionSwap = ProposeSessionSwapUseCase(sessionRepository, swapProposalRepository)
    val acceptSessionSwap = AcceptSessionSwapUseCase(swapProposalRepository, sessionRepository, syncQueueRepository)
    val completeWorkoutSet = CompleteWorkoutSetUseCase(sessionRepository, syncQueueRepository)
    val startWorkoutTimer = StartWorkoutTimerUseCase()
    val resumeWorkoutTimer = ResumeWorkoutTimerUseCase(startWorkoutTimer)
    val saveSessionProgress = SaveSessionProgressUseCase(sessionProgressRepository)
    val loadSessionProgress = LoadSessionProgressUseCase(sessionProgressRepository)
    val clearSessionProgress = ClearSessionProgressUseCase(sessionProgressRepository)
}
