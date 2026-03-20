package th.skylabmek.kmp_frontend.domain.data.repository.profile

import io.ktor.http.HttpMethod
import th.skylabmek.kmp_frontend.core.network.network_client.NetworkClient
import th.skylabmek.kmp_frontend.core.network.network_client.executeWrapped
import th.skylabmek.kmp_frontend.core.network.request.RequestSpec
import th.skylabmek.kmp_frontend.core.network.result.NetworkResult
import th.skylabmek.kmp_frontend.domain.model.performances.PerformanceListResult
import th.skylabmek.kmp_frontend.domain.model.profile.AnnounceResponse
import th.skylabmek.kmp_frontend.domain.model.profile.LifeStatus
import th.skylabmek.kmp_frontend.domain.model.profile.PerformanceGroup
import th.skylabmek.kmp_frontend.domain.model.profile.ProfileResult
import th.skylabmek.kmp_frontend.domain.repository.profile.ProfileRepository

class ProfileRepositoryImpl(
    private val networkClient: NetworkClient
) : ProfileRepository {

    override suspend fun getProfile(profileId: String): NetworkResult<ProfileResult> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Get,
                path = "/profiles/$profileId"
            ),
        )
    }

    override suspend fun getPublicProfile(profileId: String): NetworkResult<ProfileResult> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Get,
                path = "/profiles/$profileId/public"
            ),
        )
    }

    override suspend fun getAnnounces(profileId: String): NetworkResult<AnnounceResponse> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Get,
                path = "/profiles/$profileId/announces"
            ),
        )
    }

    override suspend fun getLifeStatus(profileId: String): NetworkResult<LifeStatus> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Get,
                path = "/profiles/$profileId/life-status/current"
            ),
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

    override suspend fun getPerformanceGroups(profileId: String): NetworkResult<List<PerformanceGroup>> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Get,
                path = "/profiles/$profileId/performances/groups"
            )
        )
    }
}
