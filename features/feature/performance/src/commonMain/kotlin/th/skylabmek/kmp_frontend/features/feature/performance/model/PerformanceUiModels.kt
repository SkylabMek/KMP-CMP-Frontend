package th.skylabmek.kmp_frontend.features.feature.performance.model

import org.jetbrains.compose.resources.StringResource
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.*

enum class PerformanceCategoryUi(val id: String, val displayNameRes: StringResource) {
    ACHIEVEMENT("perf_cat_achievement", Res.string.feature_name_show_performance), // Using existing keys or you can add specific ones
    DEMO("perf_cat_demo", Res.string.feature_name_show_demo_prototype),
    PERFORMANCE("perf_cat_performance", Res.string.feature_name_show_performance),
    PROJECT("perf_cat_project", Res.string.feature_name_projects),
    PUBLICATION("perf_cat_publication", Res.string.feature_name_unknown),
    WORK("perf_cat_work", Res.string.feature_name_unknown);

    companion object {
        fun fromId(id: String): PerformanceCategoryUi? = entries.find { it.id == id }
    }
}

enum class VisibilityUi(val id: String, val displayNameRes: StringResource) {
    DRAFT("visibility_draft", Res.string.nav_coming_soon), // Replace with proper visibility keys if added to strings.xml
    PRIVATE("visibility_private", Res.string.profile_admin),
    PUBLIC("visibility_public", Res.string.feature_status_operational_name);

    companion object {
        fun fromId(id: String): VisibilityUi? = entries.find { it.id == id }
    }
}
