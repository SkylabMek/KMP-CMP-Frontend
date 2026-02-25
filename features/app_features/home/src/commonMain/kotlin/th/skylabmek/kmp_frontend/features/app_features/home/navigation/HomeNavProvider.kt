package th.skylabmek.kmp_frontend.features.app_features.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import th.skylabmek.kmp_frontend.features.app_features.home.presentation.ui.HomeScreen
import th.skylabmek.kmp_frontend.features.app_features.home.presentation.ui.sceen.PerformanceScreen
import th.skylabmek.kmp_frontend.navigation.tools.FeatureNavProvider
import th.skylabmek.kmp_frontend.navigation.tools.NavKey
import th.skylabmek.kmp_frontend.navigation.tools.NavigatorAccessor

class HomeNavProvider(
    private val profileId: String,
    private val appId: String
) : FeatureNavProvider {
    override fun EntryProviderScope<NavKey>.navigationBuilder() {
        entry<HomeNavKey.Home> {
            HomeScreen(
                profileId = profileId,
                appId = appId
            )
        }
        entry<HomeNavKey.PerformanceList> {
            val navigator = NavigatorAccessor.current
            PerformanceScreen(
                profileId = profileId,
                onBack = { navigator.back() }
            )
        }
    }

    override fun registerSerializers(polymorphicModuleBuilder: PolymorphicModuleBuilder<NavKey>) {
        polymorphicModuleBuilder.subclass(HomeNavKey.Home::class, HomeNavKey.Home.serializer())
        polymorphicModuleBuilder.subclass(HomeNavKey.PerformanceList::class, HomeNavKey.PerformanceList.serializer())
    }

    override fun mapUriToNavKey(uri: String): NavKey? {
        return when {
            uri == "/" || uri.endsWith("/home") -> HomeNavKey.Home
            uri.endsWith("/performances") -> HomeNavKey.PerformanceList
            else -> null
        }
    }

    override fun mapNavKeyToUri(key: NavKey): String? {
        return when (key) {
            is HomeNavKey.Home -> "/"
            is HomeNavKey.PerformanceList -> "/performances"
            else -> null
        }
    }
}
