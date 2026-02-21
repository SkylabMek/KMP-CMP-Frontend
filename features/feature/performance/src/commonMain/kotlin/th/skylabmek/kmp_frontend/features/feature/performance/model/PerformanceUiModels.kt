package th.skylabmek.kmp_frontend.features.feature.performance.model

enum class PerformanceCategoryUi(val id: String, val displayName: String, val description: String) {
    ACHIEVEMENT("perf_cat_achievement", "Achievement", "Awards, certifications, recognitions"),
    DEMO("perf_cat_demo", "Demo/Prototype", "Interactive demonstrations and prototypes of features or concepts"),
    PERFORMANCE("perf_cat_performance", "Performance", "Talks, shows, live performances"),
    PROJECT("perf_cat_project", "Project", "Personal or professional projects"),
    PUBLICATION("perf_cat_publication", "Publication", "Articles, blogs, papers"),
    WORK("perf_cat_work", "Work Experience", "Employment or freelance work");

    companion object {
        fun fromId(id: String): PerformanceCategoryUi? = values().find { it.id == id }
    }
}

enum class VisibilityUi(val id: String, val displayName: String) {
    DRAFT("visibility_draft", "Draft"),
    PRIVATE("visibility_private", "Private"),
    PUBLIC("visibility_public", "Public");

    companion object {
        fun fromId(id: String): VisibilityUi? = values().find { it.id == id }
    }
}
