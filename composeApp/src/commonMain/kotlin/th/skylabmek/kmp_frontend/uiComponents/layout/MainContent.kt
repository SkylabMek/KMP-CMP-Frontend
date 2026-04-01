package th.skylabmek.kmp_frontend.uiComponents.layout

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import org.koin.compose.viewmodel.koinViewModel
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.domain.model.app.AppConfig
import th.skylabmek.kmp_frontend.navigation.presentation.AppNavHost
import th.skylabmek.kmp_frontend.navigation.tools.*
import th.skylabmek.kmp_frontend.presentation.viewmodel.AppInitViewModel
import th.skylabmek.kmp_frontend.presentation.viewmodel.MainContentViewModel
import th.skylabmek.kmp_frontend.ui.config.LocalAppConfig
import th.skylabmek.kmp_frontend.ui.config.ProvideUiConfig
import th.skylabmek.kmp_frontend.ui.navigation.NavItemIcon
import th.skylabmek.kmp_frontend.ui.theme.AppMaterialTheme

/**
 * Main UI content wrapper that handles navigation provision and layout.
 */
@Composable
fun MainContent(
    profileId: String,
    backStack: NavBackStack<NavKey>,
    providers: List<FeatureNavProvider>,
    navigationItems: List<NavItemIcon?>,
    deepLinkUri: String? = null,
    onDeepLinkConsumed: () -> Unit = {},
    viewModel: MainContentViewModel = koinViewModel(),
    appInitViewModel: AppInitViewModel = koinViewModel()
) {
    val lifeStatusState by viewModel.lifeStatusState.collectAsState()
    val themeSetting by viewModel.themeSetting.collectAsState()
    // not apply yet
    val configState by appInitViewModel.appConfigUiState.collectAsState()

    // Sync backstack with browser history on web platforms
    backStack.SyncWithBrowser(providers)

    // Load profile data when profileId changes
    LaunchedEffect(profileId) {
        viewModel.loadProfileData(profileId)
    }

    CompositionLocalProvider(
        LocalAppConfig provides configState
    ) {
        BoxWithConstraints {
            ProvideUiConfig(
                maxWidth = maxWidth,
                maxHeight = maxHeight,
                themeSetting = themeSetting,
                onThemeSettingChange = { viewModel.setThemeSetting(it) }
            ) {
                // Wrap the entire app structure with AppMaterialTheme so it can access UI.isDark
                AppMaterialTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ProvideNavigator(backStack = backStack, providers = providers) {
                            val navigator = NavigatorAccessor.current

                            LaunchedEffect(deepLinkUri) {
                                deepLinkUri?.let {
                                    navigator.handleDeepLink(it)
                                    onDeepLinkConsumed()
                                }
                            }

                            AppLayoutDefault(
                                lifeStatusState = lifeStatusState,
                                navigationItems = navigationItems
                            ) {
                                AppNavHost(
                                    backStack = backStack,
                                    onBack = { navigator.back() },
                                    providers = providers
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
