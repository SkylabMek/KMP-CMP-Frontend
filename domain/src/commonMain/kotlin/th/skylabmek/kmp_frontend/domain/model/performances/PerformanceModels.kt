package th.skylabmek.kmp_frontend.domain.model.performances

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PerformanceResult(
    val id: String,
    val title: String,
    @SerialName("content_url") val contentUrl: String? = null,
    @SerialName("images_tracked") val imagesTracked: Int,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class PerformanceUpdateResult(
    val id: String,
    val title: String,
    @SerialName("images_added") val imagesAdded: Int,
    @SerialName("images_removed") val imagesRemoved: Int,
    @SerialName("updated_at") val updatedAt: String
)

@Serializable
data class PerformanceDeleteResult(
    val message: String,
    @SerialName("deleted_id") val deletedId: String
)

@Serializable
data class PerformanceContentResult(
    @SerialName("content_markdown") val contentMarkdown: String
)

@Serializable
data class PerformanceContentUpdateResult(
    @SerialName("performance_id") val performanceId: String,
    @SerialName("content_url") val contentUrl: String,
    @SerialName("images_added") val imagesAdded: Int,
    @SerialName("images_removed") val imagesRemoved: Int
)

@Serializable
data class CreatePerformanceRequest(
    @SerialName("category_id") val categoryId: String,
    @SerialName("visibility_id") val visibilityId: String,
    val title: String,
    val summary: String? = null,
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("end_date") val endDate: String? = null,
    val location: String? = null
)

@Serializable
data class UpdatePerformanceRequest(
    @SerialName("category_id") val categoryId: String,
    @SerialName("visibility_id") val visibilityId: String,
    val title: String,
    val summary: String? = null,
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("end_date") val endDate: String? = null,
    val location: String? = null,
    val close: Boolean
)

@Serializable
data class UpdatePerformanceContentRequest(
    @SerialName("content_markdown") val contentMarkdown: String
)

@Serializable
data class Performance(
    val id: String,
    @SerialName("profile_id") val profileId: String,
    @SerialName("category_id") val categoryId: String,
    @SerialName("visibility_id") val visibilityId: String,
    val title: String,
    val summary: String? = null,
    @SerialName("content_url") val contentUrl: String? = null,
    @SerialName("content_type") val contentType: String,
    @SerialName("content_preview") val contentPreview: String? = null,
    @SerialName("start_date") val startDate: String? = null,
    @SerialName("end_date") val endDate: String? = null,
    val location: String? = null,
    val close: Boolean,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String? = null
)

@Serializable
data class PerformanceListResult(
    val performances: List<Performance>
)

@Serializable
data class ImageResult(
    val id: String,
    @SerialName("storage_url") val storageUrl: String,
    val filename: String,
    @SerialName("original_filename") val originalFilename: String,
    val width: Int? = null,
    val height: Int? = null,
    @SerialName("file_size") val fileSize: Int,
    @SerialName("mime_type") val mimeType: String,
    @SerialName("alt_text") val altText: String? = null,
    val caption: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("usage_count") val usageCount: Int? = null
)

@Serializable
data class ImageListResult(
    val images: List<ImageResult>,
    val total: Int,
    val limit: Int,
    val offset: Int
)

@Serializable
data class MessageResult(
    val message: String,
    val id: String? = null
)
