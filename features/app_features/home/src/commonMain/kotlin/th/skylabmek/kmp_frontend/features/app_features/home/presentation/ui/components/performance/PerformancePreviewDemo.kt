package th.skylabmek.kmp_frontend.features.app_features.home.presentation.ui.components.performance

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.stringResource
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.domain.model.feature.FeatureCode
import th.skylabmek.kmp_frontend.domain.model.feature.FeatureStatusCode
import th.skylabmek.kmp_frontend.features.feature.app.presentation.viewmodel.AppViewModel
import th.skylabmek.kmp_frontend.features.feature.performance.model.PerformanceCategoryUi
import th.skylabmek.kmp_frontend.features.app_features.profile.presentation.viewmodel.ProfileViewModel
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.*
import th.skylabmek.kmp_frontend.ui.components.card.appCard.AppElevatedCard
import th.skylabmek.kmp_frontend.ui.components.feature.FeatureStatusStripped
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultErrorContent
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultLoadingContent
import th.skylabmek.kmp_frontend.ui.dimens.Dimens

@Composable
fun PerformancePreviewDemo(
    appViewModel: AppViewModel,
    profileViewModel: ProfileViewModel,
    appId: String,
    profileId: String,
    modifier: Modifier = Modifier
) {
    val featureStatusState by appViewModel.getFeatureStatusByCode(appId, FeatureCode.SHOW_PERFORMANCE).collectAsState()
    val profileUiState by profileViewModel.performancesState.collectAsState()

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
        Text(
            text = stringResource(Res.string.feature_name_show_demo_prototype),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        AppElevatedCard {
            Column(modifier = Modifier.fillMaxWidth().padding(Dimens.paddingMedium)) {
                when (val state = featureStatusState) {
                    is UiState.Loading -> {
                        DefaultLoadingContent(modifier = Modifier.height(Dimens.containerMaxWidth / 15))
                    }
                    is UiState.Success -> {
                        val statusCode = state.data
                        if (statusCode == FeatureStatusCode.OPERATIONAL) {
                            PerformancePreviewGridContent(
                                profileUiState = profileUiState,
                                profileId = profileId,
                                onRetry = { profileViewModel.loadProfileBasicData(profileId) },
                                filter = { it.categoryId == PerformanceCategoryUi.DEMO.id },
                                maxItems = 3,
                                emptyMessage = "No demos currently available."
                            )
                        } else {
                            FeatureStatusStripped(
                                statusCode = statusCode
                            )
                        }
                    }
                    is UiState.Error -> {
                        DefaultErrorContent(
                            error = state.uiError,
                            onRetry = { /* Handle feature status refresh */ }
                        )
                    }
                }
            }
        }
    }
}
