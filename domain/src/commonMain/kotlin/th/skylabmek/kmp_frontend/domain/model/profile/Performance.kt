package th.skylabmek.kmp_frontend.domain.model.profile

import kotlinx.serialization.Serializable
import th.skylabmek.kmp_frontend.domain.model.performances.Performance

@Serializable
data class PerformanceGroup(
    val category: String,
    val items: List<Performance>
)
