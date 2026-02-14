package th.skylabmek.kmp_frontend.domain.data.repository.performances

import io.ktor.http.HttpMethod
import th.skylabmek.kmp_frontend.core.network.network_client.NetworkClient
import th.skylabmek.kmp_frontend.core.network.network_client.executeWrapped
import th.skylabmek.kmp_frontend.core.network.request.RequestSpec
import th.skylabmek.kmp_frontend.core.network.result.NetworkResult
import th.skylabmek.kmp_frontend.domain.model.performances.*
import th.skylabmek.kmp_frontend.domain.repository.performances.PerformanceRepository

class PerformanceRepositoryImpl(
    private val networkClient: NetworkClient
) : PerformanceRepository {

    override suspend fun getPerformances(profileId: String): NetworkResult<PerformanceListResult> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Get,
                path = "/profiles/$profileId/performances"
            )
        )
    }

    override suspend fun getPublicPerformances(profileId: String): NetworkResult<PerformanceListResult> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Get,
                path = "/profiles/$profileId/publicPerformances"
            )
        )
    }

    override suspend fun createPerformance(
        profileId: String,
        request: CreatePerformanceRequest
    ): NetworkResult<PerformanceResult> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Post,
                path = "/profiles/$profileId/performances",
                body = request
            )
        )
    }

    override suspend fun updatePerformance(
        profileId: String,
        performanceId: String,
        request: UpdatePerformanceRequest
    ): NetworkResult<PerformanceUpdateResult> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Patch,
                path = "/profiles/$profileId/performances/$performanceId",
                body = request
            )
        )
    }

    override suspend fun deletePerformance(
        profileId: String,
        performanceId: String
    ): NetworkResult<PerformanceDeleteResult> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Delete,
                path = "/profiles/$profileId/performances/$performanceId"
            )
        )
    }

    override suspend fun getPerformanceContent(
        profileId: String,
        performanceId: String
    ): NetworkResult<PerformanceContentResult> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Get,
                path = "/profiles/$profileId/performances/$performanceId/content"
            )
        )
    }

    override suspend fun updatePerformanceContent(
        profileId: String,
        performanceId: String,
        request: UpdatePerformanceContentRequest
    ): NetworkResult<PerformanceContentUpdateResult> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Patch,
                path = "/profiles/$profileId/performances/$performanceId/content",
                body = request
            )
        )
    }
}
