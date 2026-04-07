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

    suspend fun getImageDetails(
        profileId: String,
        imageId: String
    ): NetworkResult<ImageResult>

    suspend fun uploadImage(
        profileId: String,
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        altText: String? = null,
        caption: String? = null
    ): NetworkResult<ImageResult>

    suspend fun updateImageMetadata(
        profileId: String,
        imageId: String,
        altText: String?,
        caption: String?
    ): NetworkResult<MessageResult>

    suspend fun deleteImage(
        profileId: String,
        imageId: String
    ): NetworkResult<MessageResult>

    suspend fun forceDeleteImage(
        profileId: String,
        imageId: String
    ): NetworkResult<MessageResult>

    suspend fun getPerformanceImages(
        profileId: String,
        performanceId: String
    ): NetworkResult<PerformanceImagesResult>

    suspend fun getImagePerformances(
        profileId: String,
        imageId: String
    ): NetworkResult<ImageUsageResult>

    suspend fun trackImageUsage(
        profileId: String,
        request: TrackImageUsageRequest
    ): NetworkResult<MessageResult>

    suspend fun untrackImageUsage(
        profileId: String,
        request: TrackImageUsageRequest
    ): NetworkResult<MessageResult>

    suspend fun getUnusedImages(
        profileId: String,
        daysOld: Int? = null
    ): NetworkResult<UnusedImagesResult>

    suspend fun deleteUnusedImages(
        profileId: String,
        confirm: Boolean,
        daysOld: Int? = null
    ): NetworkResult<DeleteUnusedImagesResult>
}
