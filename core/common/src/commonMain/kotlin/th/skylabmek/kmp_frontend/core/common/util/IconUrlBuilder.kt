package th.skylabmek.kmp_frontend.core.common.util

import th.skylabmek.kmp_frontend.core.common.AppBuildKonfig

object IconUrlBuilder {
    fun buildBrandfetchUrl(
        iconBaseUrl: String,
        domain: String,
        width: Int = 400,
        height: Int = 400
    ): String {
        // Expected format: cdn.brandfetch.io/domain/youtube.com/w/400/h/400?c=1idi9KbP-wRzPrDwdwW
        // iconBaseUrl should be "cdn.brandfetch.io" or similar from AppConfig
        val clientId = AppBuildKonfig.BRANDFETCH_CLIENT_ID
        
        return "$iconBaseUrl/$domain/w/$width/h/$height?c=$clientId"
    }
}
