package th.skylabmek.kmp_frontend.features.app_features.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import th.skylabmek.kmp_frontend.features.app_features.home.presentation.ui.HomeScreen
import th.skylabmek.kmp_frontend.navigation.tools.FeatureNavProvider
import th.skylabmek.kmp_frontend.navigation.tools.NavKey

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
    }

    override fun registerSerializers(polymorphicModuleBuilder: PolymorphicModuleBuilder<NavKey>) {
        polymorphicModuleBuilder.subclass(HomeNavKey.Home::class, HomeNavKey.Home.serializer())
    }

    override fun mapUriToNavKey(uri: String): NavKey? {
        return if (uri == "/" || uri.endsWith("/home")) HomeNavKey.Home else null
    }

    override fun mapNavKeyToUri(key: NavKey): String? {
        return if (key is HomeNavKey.Home) "/" else null
    }
}
