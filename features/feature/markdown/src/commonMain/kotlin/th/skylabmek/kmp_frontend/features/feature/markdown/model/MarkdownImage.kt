package th.skylabmek.kmp_frontend.features.feature.markdown.model

data class MarkdownImage(
    val imageId: String,
    val filename: String,
    val imageUrl: String,
    val usageCount: Int = 0,
    val performances: List<MarkdownImagePerformance>? = null
)

data class MarkdownImagePerformance(
    val performanceId: String,
    val title: String,
    val usageCount: Int,
    val firstUsedAt: String,
    val lastUsedAt: String
)
