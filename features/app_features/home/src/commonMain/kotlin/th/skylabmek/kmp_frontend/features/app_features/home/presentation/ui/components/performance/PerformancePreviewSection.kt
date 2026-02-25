package th.skylabmek.kmp_frontend.features.app_features.home.presentation.ui.components.performance

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import org.jetbrains.compose.resources.stringResource
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.domain.model.feature.FeatureCode
import th.skylabmek.kmp_frontend.domain.model.feature.FeatureStatusCode
import th.skylabmek.kmp_frontend.features.feature.app.presentation.viewmodel.AppViewModel
import th.skylabmek.kmp_frontend.features.feature.performance.model.PerformanceCategoryUi
import th.skylabmek.kmp_frontend.features.app_features.profile.presentation.viewmodel.ProfileViewModel
import th.skylabmek.kmp_frontend.features.app_features.home.navigation.HomeNavKey
import th.skylabmek.kmp_frontend.navigation.tools.NavigatorAccessor
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.*
import th.skylabmek.kmp_frontend.ui.components.card.appCard.AppElevatedCard
import th.skylabmek.kmp_frontend.ui.components.feature.FeatureStatusStripped
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultErrorContent
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultLoadingContent
import th.skylabmek.kmp_frontend.ui.dimens.Dimens

@Composable
fun PerformancePreviewSection(
    appViewModel: AppViewModel,
    profileViewModel: ProfileViewModel,
    appId: String,
    profileId: String,
    modifier: Modifier = Modifier
) {
    val navigator = NavigatorAccessor.current
    
    // Ensure data is fetched when this section is composed
    LaunchedEffect(profileId) {
        profileViewModel.loadProfileBasicData(profileId)
    }

    val featureStatusState by appViewModel.getFeatureStatusByCode(
        appId,
        FeatureCode.SHOW_PERFORMANCE
    ).collectAsState()
    val profileUiState by profileViewModel.performancesState.collectAsState()

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
                        when (val statusCode = state.data) {
                            FeatureStatusCode.OPERATIONAL -> {
                                PerformancePreviewGridContent(
                                    profileUiState = profileUiState,
                                    profileId = profileId,
                                    onRetry = { profileViewModel.loadProfileBasicData(profileId) },
                                    filter = { it.categoryId != PerformanceCategoryUi.DEMO.id },
                                    maxItems = 6,
                                    emptyMessage = "No performances available.",
                                    header = {
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
                                            TextButton(onClick = { navigator.navigate(HomeNavKey.PerformanceList) }) {
                                                Text(stringResource(Res.string.performance_preview_see_all))
                                            }
                                        }
                                    }
                                )
                            }

                            FeatureStatusCode.UNDER_CONSTRUCTION -> {
                                PerformancePreviewConstruction(statusCode)
                            }

                            else -> {
                                FeatureStatusStripped(
                                    statusCode = statusCode
                                )
                            }
                        }
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
private fun PerformancePreviewConstruction(statusCode: FeatureStatusCode) {
    val uriHandler = LocalUriHandler.current
    val notionUrl = stringResource(Res.string.url_temp_portfolio)
    val oldSiteUrl = stringResource(Res.string.url_old_website)

    Column(
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
    ) {
        // Integrating FeatureStatusStripped at the top
        FeatureStatusStripped(statusCode = statusCode)

        HorizontalDivider(
            modifier = Modifier.padding(vertical = Dimens.spaceExtraSmall),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(Res.string.performance_preview_construction_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(Res.string.performance_preview_construction_msg),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
                modifier = Modifier.padding(top = Dimens.spaceSmall)
            ) {
                Button(
                    onClick = { uriHandler.openUri(notionUrl) }
                ) {
                    Text(stringResource(Res.string.performance_preview_visit_notion))
                }

                OutlinedButton(
                    onClick = { uriHandler.openUri(oldSiteUrl) }
                ) {
                    Text(stringResource(Res.string.performance_preview_visit_old_site))
                }
            }
        }
    }

}
