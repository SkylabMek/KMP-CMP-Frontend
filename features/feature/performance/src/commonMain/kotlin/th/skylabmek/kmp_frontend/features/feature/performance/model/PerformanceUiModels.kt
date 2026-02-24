package th.skylabmek.kmp_frontend.features.feature.performance.model

import org.jetbrains.compose.resources.StringResource
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.*

enum class PerformanceCategoryUi(
    val id: String,
    val displayNameRes: StringResource,
    val displayDescRes: StringResource
) {
    ACHIEVEMENT("perf_cat_achievement", Res.string.perf_cat_achievement_name, Res.string.perf_cat_achievement_desc),
    DEMO("perf_cat_demo", Res.string.perf_cat_demo_name, Res.string.perf_cat_demo_desc),
    PERFORMANCE("perf_cat_performance", Res.string.perf_cat_performance_name, Res.string.perf_cat_performance_desc),
    PROJECT("perf_cat_project", Res.string.perf_cat_project_name, Res.string.perf_cat_project_desc),
    PUBLICATION("perf_cat_publication", Res.string.perf_cat_publication_name, Res.string.perf_cat_publication_desc),
    WORK("perf_cat_work", Res.string.perf_cat_work_name, Res.string.perf_cat_work_desc);

    companion object {
        fun fromId(id: String): PerformanceCategoryUi? = values().find { it.id == id }
    }
}

enum class VisibilityUi(val id: String, val displayNameRes: StringResource) {
    DRAFT("visibility_draft", Res.string.visibility_draft_name),
    PRIVATE("visibility_private", Res.string.visibility_private_name),
    PUBLIC("visibility_public", Res.string.visibility_public_name);

    companion object {
        fun fromId(id: String): VisibilityUi? = values().find { it.id == id }
    }
}
