package com.atlan.performance.shared.domain.repository

import com.atlan.performance.shared.domain.model.TrainingPlan
import com.atlan.performance.shared.domain.model.TrainingWeek

interface TrainingPlanRepository {
    suspend fun getPlan(): TrainingPlan
    suspend fun getCurrentWeek(): TrainingWeek?
}
