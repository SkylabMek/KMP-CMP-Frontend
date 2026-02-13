package th.skylabmek.kmp_frontend.core.common.util

import kotlin.time.Clock
import th.skylabmek.kmp_frontend.core.data_local.domain.LocalSettingsRepository

fun isUserLoggedIn(localSettings: LocalSettingsRepository): Boolean {
    val accessToken = localSettings.getString("access_token", "")
    val expiresAtStr = localSettings.getString("expires_at", "0")
    val expiresAt = expiresAtStr.toLongOrNull() ?: 0L
    val currentTime = Clock.System.now().toEpochMilliseconds()
    
    return accessToken.isNotEmpty() && (currentTime < expiresAt)
}

fun shouldRefreshToken(localSettings: LocalSettingsRepository): Boolean {
    val refreshToken = localSettings.getString("refresh_token", "")
    val expiresAtStr = localSettings.getString("expires_at", "0")
    val expiresAt = expiresAtStr.toLongOrNull() ?: 0L
    val currentTime = Clock.System.now().toEpochMilliseconds()
    
    // Refresh if it's within 5 minutes of expiring
    val fiveMinutesInMillis = 5 * 60 * 1000L
    val timeToExpiry = expiresAt - currentTime
    return refreshToken.isNotEmpty() && expiresAt > 0 && timeToExpiry < fiveMinutesInMillis
}

fun isRefreshTokenExpired(localSettings: LocalSettingsRepository): Boolean {
    val refreshExpiresAtStr = localSettings.getString("refresh_expires_at", "0")
    val refreshExpiresAt = refreshExpiresAtStr.toLongOrNull() ?: 0L
    if (refreshExpiresAt <= 0) return false // Assume not expired if not set (legacy support)
    
    val currentTime = Clock.System.now().toEpochMilliseconds()
    return currentTime >= refreshExpiresAt
}
