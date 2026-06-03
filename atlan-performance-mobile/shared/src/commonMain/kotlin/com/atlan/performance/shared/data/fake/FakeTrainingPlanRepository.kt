package com.atlan.performance.shared.data.fake

import com.atlan.performance.shared.data.seed.SeedTrainingPlan
import com.atlan.performance.shared.domain.model.TrainingPlan
import com.atlan.performance.shared.domain.model.TrainingWeek
import com.atlan.performance.shared.domain.repository.TrainingPlanRepository

class FakeTrainingPlanRepository(
    private val plan: TrainingPlan = SeedTrainingPlan.plan
) : TrainingPlanRepository {

    override suspend fun getPlan(): TrainingPlan = plan

    override suspend fun getCurrentWeek(): TrainingWeek? = plan.currentWeek
}
