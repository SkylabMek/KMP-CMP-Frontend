package th.skylabmek.kmp_frontend.domain.repository.performances

import th.skylabmek.kmp_frontend.core.network.result.NetworkResult
import th.skylabmek.kmp_frontend.domain.model.performances.*

interface PerformanceRepository {
    suspend fun getPerformances(
        profileId: String
    ): NetworkResult<PerformanceListResult>

    suspend fun getPublicPerformances(
        profileId: String
    ): NetworkResult<PerformanceListResult>

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
    
    suspend fun getPerformanceContentFromUrl(
        url: String
    ): NetworkResult<String>

    suspend fun updatePerformanceContent(
        profileId: String,
        performanceId: String,
        request: UpdatePerformanceContentRequest
    ): NetworkResult<PerformanceContentUpdateResult>

    suspend fun getImages(
        profileId: String,
        search: String? = null,
        limit: Int? = null,
        offset: Int? = null
    ): NetworkResult<ImageListResult>

    suspend fun uploadImage(
        profileId: String,
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        altText: String? = null,
        caption: String? = null
    ): NetworkResult<ImageResult>

    suspend fun forceDeleteImage(
        profileId: String,
        imageId: String
    ): NetworkResult<MessageResult>
}
