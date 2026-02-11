package th.skylabmek.kmp_frontend.features.profile.navigation

import kotlinx.serialization.Serializable
import th.skylabmek.kmp_frontend.navigation.tools.NavKey

@Serializable
sealed interface ProfileNavKey : NavKey {
    @Serializable
    data object Profile : ProfileNavKey

    @Serializable
    data object Login : ProfileNavKey
}
