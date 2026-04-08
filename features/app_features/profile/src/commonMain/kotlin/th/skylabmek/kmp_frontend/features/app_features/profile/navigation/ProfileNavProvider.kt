package th.skylabmek.kmp_frontend.features.app_features.profile.navigation

import androidx.navigation3.runtime.EntryProviderScope
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import org.koin.compose.viewmodel.koinViewModel
import th.skylabmek.kmp_frontend.features.feature.app.presentation.viewmodel.AppViewModel
import th.skylabmek.kmp_frontend.features.app_features.profile.presentation.ui.admin.performance.PerformanceScreenAdmin
import th.skylabmek.kmp_frontend.features.app_features.profile.presentation.ui.admin.profile.LoginScreen
import th.skylabmek.kmp_frontend.features.app_features.profile.presentation.ui.admin.profile.ProfileScreenAdmin
import th.skylabmek.kmp_frontend.features.app_features.profile.presentation.ui.publicUI.ProfileScreenPublicUi
import th.skylabmek.kmp_frontend.navigation.tools.FeatureNavProvider
import th.skylabmek.kmp_frontend.navigation.tools.NavKey

class ProfileNavProvider(
    private val profileId: String
) : FeatureNavProvider {
    override fun EntryProviderScope<NavKey>.navigationBuilder() {
        entry<ProfileNavKey.Profile> {
            ProfileScreenPublicUi(
                viewModel = koinViewModel(),
                profileId = profileId
            )
        }
        entry<ProfileNavKey.ProfileAdmin> {
            ProfileScreenAdmin(
                viewModel = koinViewModel(),
                profileId = profileId
            )
        }
        entry<ProfileNavKey.Login> {
            LoginScreen()
        }
        entry<ProfileNavKey.PerformanceAdmin> { key ->
            val appViewModel: AppViewModel = koinViewModel()
            PerformanceScreenAdmin(
                appViewModel = appViewModel,
                profileId = key.profileId
            )
        }
    }

    override fun registerSerializers(polymorphicModuleBuilder: PolymorphicModuleBuilder<NavKey>) {
        polymorphicModuleBuilder.subclass(ProfileNavKey.Profile::class, ProfileNavKey.Profile.serializer())
        polymorphicModuleBuilder.subclass(ProfileNavKey.ProfileAdmin::class, ProfileNavKey.ProfileAdmin.serializer())
        polymorphicModuleBuilder.subclass(ProfileNavKey.Login::class, ProfileNavKey.Login.serializer())
        polymorphicModuleBuilder.subclass(ProfileNavKey.PerformanceAdmin::class, ProfileNavKey.PerformanceAdmin.serializer())
    }

    override fun mapUriToNavKey(uri: String): NavKey? {
        val path = uri.substringBefore("?").removePrefix("#")
        return when {
            path == "/profile" -> ProfileNavKey.Profile
            path == "/profile/admin" -> ProfileNavKey.ProfileAdmin
            path == "/login" -> ProfileNavKey.Login
            path.startsWith("/performance/") -> {
                val id = path.substringAfterLast("/")
                ProfileNavKey.PerformanceAdmin(id)
            }
            else -> null
        }
    }

    override fun mapNavKeyToUri(key: NavKey): String? {
        return when (key) {
            is ProfileNavKey.Profile -> "/profile"
            is ProfileNavKey.ProfileAdmin -> "/profile/admin"
            is ProfileNavKey.Login -> "/login"
            is ProfileNavKey.PerformanceAdmin -> "/performance/${key.profileId}"
            else -> null
        }
    }
}
