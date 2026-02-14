package th.skylabmek.kmp_frontend.features.profile.navigation

import androidx.navigation3.runtime.EntryProviderScope
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import org.koin.compose.viewmodel.koinViewModel
import th.skylabmek.kmp_frontend.features.app.presentation.viewmodel.AppViewModel
import th.skylabmek.kmp_frontend.features.profile.presentation.ui.performance.PerformanceScreen
import th.skylabmek.kmp_frontend.features.profile.presentation.ui.profile.LoginScreen
import th.skylabmek.kmp_frontend.features.profile.presentation.ui.profile.ProfileScreen
import th.skylabmek.kmp_frontend.navigation.tools.FeatureNavProvider
import th.skylabmek.kmp_frontend.navigation.tools.NavKey

class ProfileNavProvider(
    private val profileId: String
) : FeatureNavProvider {
    override fun EntryProviderScope<NavKey>.navigationBuilder() {
        entry<ProfileNavKey.Profile> {
            ProfileScreen(
                viewModel = koinViewModel(),
                profileId = profileId
            )
        }
        entry<ProfileNavKey.Login> {
            LoginScreen()
        }
        entry<ProfileNavKey.Performance> { key ->
            val appViewModel: AppViewModel = koinViewModel()
            PerformanceScreen(
                appViewModel = appViewModel,
                profileId = key.profileId
            )
        }
    }

    override fun registerSerializers(polymorphicModuleBuilder: PolymorphicModuleBuilder<NavKey>) {
        polymorphicModuleBuilder.subclass(ProfileNavKey.Profile::class, ProfileNavKey.Profile.serializer())
        polymorphicModuleBuilder.subclass(ProfileNavKey.Login::class, ProfileNavKey.Login.serializer())
        polymorphicModuleBuilder.subclass(ProfileNavKey.Performance::class, ProfileNavKey.Performance.serializer())
    }

    override fun mapUriToNavKey(uri: String): NavKey? {
        return when {
            uri.endsWith("/profile") -> ProfileNavKey.Profile
            uri.endsWith("/login") -> ProfileNavKey.Login
            uri.contains("/performance/") -> {
                val id = uri.substringAfterLast("/")
                ProfileNavKey.Performance(id)
            }
            else -> null
        }
    }

    override fun mapNavKeyToUri(key: NavKey): String? {
        return when (key) {
            is ProfileNavKey.Profile -> "/profile"
            is ProfileNavKey.Login -> "/login"
            is ProfileNavKey.Performance -> "/performance/${key.profileId}"
            else -> null
        }
    }
}
