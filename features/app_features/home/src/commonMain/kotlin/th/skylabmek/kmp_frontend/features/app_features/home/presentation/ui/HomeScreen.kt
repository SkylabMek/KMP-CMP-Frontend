package th.skylabmek.kmp_frontend.features.app_features.home.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import th.skylabmek.kmp_frontend.features.feature.app.presentation.viewmodel.AppViewModel
import th.skylabmek.kmp_frontend.features.app_features.home.presentation.ui.components.performance.PerformancePreviewDemo
import th.skylabmek.kmp_frontend.features.app_features.home.presentation.ui.components.performance.PerformancePreviewSection
import th.skylabmek.kmp_frontend.features.app_features.profile.presentation.viewmodel.ProfileViewModel
import th.skylabmek.kmp_frontend.ui.config.UI
import th.skylabmek.kmp_frontend.ui.dimens.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    appViewModel: AppViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel(),
    profileId: String,
    appId: String,
) {
    val profileUiState by profileViewModel.uiState.collectAsState()

    LaunchedEffect(profileId, appId) {
        appViewModel.getOrLoadFeatureStatus(appId)
        profileViewModel.getOrLoadProfileBasicData(profileId)
        profileViewModel.getOrLoadPerformances(profileId)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isDesktop = UI.isDesktop

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.screenPadding)
        ) {
            if (isDesktop) {
                DesktopLayout(
                    appViewModel = appViewModel,
                    profileViewModel = profileViewModel,
                    appId = appId,
                    profileId = profileId
                )
            } else {
                MobileLayout(
                    appViewModel = appViewModel,
                    profileViewModel = profileViewModel,
                    appId = appId,
                    profileId = profileId
                )
            }
        }
    }
}

@Composable
fun MobileLayout(
    appViewModel: AppViewModel,
    profileViewModel: ProfileViewModel,
    appId: String,
    profileId: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceLarge)
    ) {
        PerformancePreviewDemo(
            appViewModel = appViewModel,
            profileViewModel = profileViewModel,
            appId = appId,
            profileId = profileId
        )

        PerformancePreviewSection(
            appViewModel = appViewModel,
            profileViewModel = profileViewModel,
            appId = appId,
            profileId = profileId
        )
    }
}

@Composable
fun DesktopLayout(
    appViewModel: AppViewModel,
    profileViewModel: ProfileViewModel,
    appId: String,
    profileId: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceLarge)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            PerformancePreviewDemo(
                appViewModel = appViewModel,
                profileViewModel = profileViewModel,
                appId = appId,
                profileId = profileId
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            PerformancePreviewSection(
                appViewModel = appViewModel,
                profileViewModel = profileViewModel,
                appId = appId,
                profileId = profileId
            )
        }
    }
}
