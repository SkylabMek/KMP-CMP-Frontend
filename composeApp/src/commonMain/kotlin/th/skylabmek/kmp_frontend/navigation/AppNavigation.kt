package th.skylabmek.kmp_frontend.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.rememberNavBackStack
import th.skylabmek.kmp_frontend.features.app_features.home.navigation.HomeNavKey
import th.skylabmek.kmp_frontend.features.app_features.home.navigation.HomeNavProvider
import th.skylabmek.kmp_frontend.features.app_features.profile.navigation.ProfileNavKey
import th.skylabmek.kmp_frontend.features.app_features.profile.navigation.ProfileNavProvider
import th.skylabmek.kmp_frontend.navigation.model.createNavigationConfig
import th.skylabmek.kmp_frontend.navigation.tools.FeatureNavProvider
import th.skylabmek.kmp_frontend.navigation.tools.NavKey
import th.skylabmek.kmp_frontend.ui.navigation.NavItemIcon

/**
 * State holder for the application's navigation.
 */
class AppNavigationState(
    val backStack: NavBackStack<NavKey>,
    val providers: List<FeatureNavProvider>,
    val navItems: List<NavItemIcon?>
)

/**
 * Creates and remembers the application navigation state.
 * 
 * @param profileId The profile identifier to be used by feature providers.
 * @param appId The application identifier to be used by feature providers.
 */
@Composable
fun rememberAppNavigationState(
    profileId: String,
    appId: String
): AppNavigationState {
    // 1. Setup feature providers
    val providers = remember(profileId, appId) {
        listOf(
            HomeNavProvider(
                profileId = profileId,
                appId = appId
            ),
            ProfileNavProvider(
                profileId = profileId
            )
        )
    }

    // 2. Setup navigation configuration (serialization, etc.)
    val navConfig = remember(providers) {
        createNavigationConfig(providers)
    }

    // 3. Setup the backstack
    val backStack = rememberNavBackStack(navConfig, HomeNavKey.Home)

    // 4. Setup top-level navigation items (Header/Drawer)
    val navItems = remember {
        listOf(
            NavItemIcon(
                title = "Home",
                key = HomeNavKey.Home,
                icon = Icons.Default.Home
            ),
            NavItemIcon(
                title = "Profile",
                key = ProfileNavKey.Profile,
                icon = Icons.Default.Person
            ),
            null // Placeholder for "Coming Soon"
        )
    }

    return remember(backStack, providers, navItems) {
        AppNavigationState(backStack, providers, navItems)
    }
}
