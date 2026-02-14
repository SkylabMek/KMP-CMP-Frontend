package th.skylabmek.kmp_frontend.domain.repository.profile

import th.skylabmek.kmp_frontend.core.network.result.NetworkResult
import th.skylabmek.kmp_frontend.domain.model.profile.AnnounceResponse
import th.skylabmek.kmp_frontend.domain.model.profile.LifeStatus
import th.skylabmek.kmp_frontend.domain.model.profile.Performance
import th.skylabmek.kmp_frontend.domain.model.profile.PerformanceGroup
import th.skylabmek.kmp_frontend.domain.model.profile.ProfileResult

interface ProfileRepository {
    suspend fun getProfile(profileId: String): NetworkResult<ProfileResult>
    suspend fun getAnnounces(profileId: String): NetworkResult<AnnounceResponse>
    suspend fun getLifeStatus(profileId: String): NetworkResult<LifeStatus>
    suspend fun getPerformances(profileId: String): NetworkResult<List<Performance>>
    suspend fun getPerformanceGroups(profileId: String): NetworkResult<List<PerformanceGroup>>
}
