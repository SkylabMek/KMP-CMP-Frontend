object AppConfig {
    const val PROFILE_ID = "profile_001"
    const val APP_ID = "website_main_001"

    // Environment-specific configs
    object Dev {
        const val API_BASE_URL = "http://localhost:8080"
        const val DEBUG = true
    }
    
    object Prod {
        const val API_BASE_URL = "https://api.yourwebsite.com"
        const val DEBUG = false
    }
    
    // Feature flags
    object Features {
        const val ENABLE_ANALYTICS = true
        const val ENABLE_LOGGING = true
    }
}
