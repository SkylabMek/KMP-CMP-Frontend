package th.skylabmek.kmp_frontend.features.app_features.home.presentation.ui.performance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import org.jetbrains.compose.resources.stringResource
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.domain.model.feature.FeatureCode
import th.skylabmek.kmp_frontend.domain.model.feature.FeatureStatusCode
import th.skylabmek.kmp_frontend.domain.model.profile.Performance
import th.skylabmek.kmp_frontend.features.feature.app.presentation.viewmodel.AppViewModel
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
                            DemoPreviewContent(profileUiState, profileId, profileViewModel)
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

@Composable
private fun DemoPreviewContent(
    profileUiState: UiState<List<Performance>>,
    profileId: String,
    profileViewModel: ProfileViewModel
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
        when (profileUiState) {
            is UiState.Loading -> DefaultLoadingContent(modifier = Modifier.height(Dimens.containerMaxWidth / 15))

            is UiState.Error -> {
                DefaultErrorContent(
                    error = profileUiState.uiError,
                    onRetry = { profileViewModel.loadProfileBasicData(profileId) }
                )
            }
            is UiState.Success -> {
                val demos = profileUiState.data.filter {
                    it.title.contains("Demo", ignoreCase = true) || 
                    it.title.contains("Prototype", ignoreCase = true) ||
                    it.summary?.contains("Demo", ignoreCase = true) == true
                }

                if (demos.isEmpty()) {
                    Text("No demos currently available.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                } else {
                    DemoGrid(demos = demos)
                }
            }
        }
    }
}

@Composable
private fun DemoGrid(demos: List<Performance>) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.itemSpacingSmall)) {
        demos.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.itemSpacingSmall)) {
                rowItems.forEach { demo ->
                    DemoItem(
                        performance = demo,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DemoItem(performance: Performance, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(Dimens.containerMaxWidth / 15),
        shape = RoundedCornerShape(Dimens.cardCornerRadiusSmall),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(Dimens.paddingSmall),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Text(
                    text = performance.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Interactive Demo",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
