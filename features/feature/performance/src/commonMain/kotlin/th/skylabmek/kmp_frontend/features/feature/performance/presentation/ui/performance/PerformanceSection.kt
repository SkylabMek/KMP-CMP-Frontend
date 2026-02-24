package th.skylabmek.kmp_frontend.features.feature.performance.presentation.ui.performance

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.domain.model.feature.FeatureCode
import th.skylabmek.kmp_frontend.domain.model.feature.FeatureStatusCode
import th.skylabmek.kmp_frontend.domain.model.performances.Performance
import th.skylabmek.kmp_frontend.features.feature.app.presentation.viewmodel.AppViewModel
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.components.performance.PerformanceGrid
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.viewmodel.PerformanceListViewModel
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.*
import th.skylabmek.kmp_frontend.ui.components.card.appCard.AppElevatedCard
import th.skylabmek.kmp_frontend.ui.components.feature.FeatureStatusStripped
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultErrorContent
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultLoadingContent
import th.skylabmek.kmp_frontend.ui.dimens.Dimens

@Composable
fun PerformanceSection(
    appViewModel: AppViewModel,
    onPerformanceClick: (Performance) -> Unit,
    performanceListViewModel: PerformanceListViewModel = koinViewModel(),
    appId: String,
    profileId: String,
    modifier: Modifier = Modifier
) {
    val featureStatusState by appViewModel.getFeatureStatusByCode(
        appId,
        FeatureCode.SHOW_PERFORMANCE
    ).collectAsState()
    val performanceUiState by performanceListViewModel.performancesState.collectAsState()

    LaunchedEffect(profileId) {
        performanceListViewModel.loadPerformances(profileId)
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
        Text(
            text = stringResource(Res.string.feature_name_show_performance),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        AppElevatedCard {
            Column(modifier = Modifier.fillMaxWidth().padding(Dimens.paddingMedium)) {
                when (val state = featureStatusState) {
                    is UiState.Loading -> {
                        DefaultLoadingContent(modifier = Modifier.height(Dimens.containerMaxWidth / 10))
                    }

                    is UiState.Success -> {
                        PerformanceContent(
                            performanceUiState = performanceUiState,
                            profileId = profileId,
                            performanceListViewModel = performanceListViewModel,
                            onPerformanceClick = onPerformanceClick
                        )
                    }

                    is UiState.Error -> {
                        DefaultErrorContent(
                            error = state.uiError,
                            onRetry = { /* Refresh feature status */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PerformanceContent(
    performanceUiState: UiState<List<Performance>>,
    profileId: String,
    performanceListViewModel: PerformanceListViewModel,
    onPerformanceClick: (Performance) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.feature_name_show_performance),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        when (performanceUiState) {
            is UiState.Loading -> DefaultLoadingContent(modifier = Modifier.height(Dimens.containerMaxWidth / 12))

            is UiState.Error -> {
                DefaultErrorContent(
                    error = performanceUiState.uiError,
                    onRetry = { performanceListViewModel.loadPerformances(profileId) }
                )
            }

            is UiState.Success -> {
                val performances = performanceUiState.data
                PerformanceGrid(
                    performances = performances,
                    onPerformanceClick = onPerformanceClick
                )
            }
        }
    }
}
