package th.skylabmek.kmp_frontend.features.app_features.home.navigation

import kotlinx.serialization.Serializable
import th.skylabmek.kmp_frontend.navigation.tools.NavKey

@Serializable
sealed interface HomeNavKey : NavKey {
    @Serializable
    data object Home : HomeNavKey
}
