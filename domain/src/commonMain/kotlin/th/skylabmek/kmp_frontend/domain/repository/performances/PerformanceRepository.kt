package th.skylabmek.kmp_frontend.domain.repository.performances

import th.skylabmek.kmp_frontend.core.network.result.NetworkResult
import th.skylabmek.kmp_frontend.domain.model.performances.*

interface PerformanceRepository {
    suspend fun createPerformance(
        profileId: String,
        request: CreatePerformanceRequest
    ): NetworkResult<PerformanceResult>

    suspend fun updatePerformance(
        profileId: String,
        performanceId: String,
        request: UpdatePerformanceRequest
    ): NetworkResult<PerformanceUpdateResult>

    suspend fun deletePerformance(
        profileId: String,
        performanceId: String
    ): NetworkResult<PerformanceDeleteResult>

    suspend fun getPerformanceContent(
        profileId: String,
        performanceId: String
    ): NetworkResult<PerformanceContentResult>

    suspend fun updatePerformanceContent(
        profileId: String,
        performanceId: String,
        request: UpdatePerformanceContentRequest
    ): NetworkResult<PerformanceContentUpdateResult>
}
