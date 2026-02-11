package th.skylabmek.kmp_frontend.core.common.util

import th.skylabmek.kmp_frontend.core.data_local.domain.LocalSettingsRepository

fun isUserLoggedIn(localSettings: LocalSettingsRepository): Boolean {
    return localSettings.getString("access_token", "").isNotEmpty()
}
