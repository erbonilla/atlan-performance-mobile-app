package com.atlan.performance.shared.domain.usecase

import com.atlan.performance.shared.domain.model.TrainingWeek
import com.atlan.performance.shared.domain.repository.TrainingPlanRepository

/**
 * Returns the current training week (with its sessions) for the Workout Plan List. Calm by design —
 * the week carries a qualitative `loadStatus` ("On track"), never a deficit or behind-plan figure,
 * and completed/upcoming sessions are equal, non-pressured states.
 */
class GetTrainingPlanUseCase(
    private val trainingPlanRepository: TrainingPlanRepository
) {
    suspend operator fun invoke(): TrainingWeek? = trainingPlanRepository.getCurrentWeek()
}
