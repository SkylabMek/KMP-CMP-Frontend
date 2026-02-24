package th.skylabmek.kmp_frontend.features.app_features.home.presentation.ui.performance

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.domain.model.performances.Performance
import th.skylabmek.kmp_frontend.domain.model.performances.PerformanceListResult
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.components.performance.publicPerformance.PerformanceFullContent
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.components.performance.publicPerformance.PerformanceShowGrid
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultErrorContent
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultLoadingContent
import th.skylabmek.kmp_frontend.ui.dimens.Dimens

@Composable
fun PerformancePreviewGridContent(
    profileUiState: UiState<PerformanceListResult>,
    profileId: String,
    onRetry: () -> Unit,
    filter: (Performance) -> Boolean,
    maxItems: Int,
    emptyMessage: String,
    modifier: Modifier = Modifier,
    header: @Composable (ColumnScope.() -> Unit)? = null
) {
    var selectedPerformance by remember { mutableStateOf<Performance?>(null) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
        header?.invoke(this)

        when (profileUiState) {
            is UiState.Loading -> {
                DefaultLoadingContent(modifier = Modifier.height(Dimens.containerMaxWidth / 15))
            }
            is UiState.Error -> {
                DefaultErrorContent(
                    error = profileUiState.uiError,
                    onRetry = onRetry
                )
            }
            is UiState.Success -> {
                val performances = profileUiState.data.performances
                    .filter(filter)
                    .take(maxItems)

                if (performances.isEmpty()) {
                    Text(
                        text = emptyMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    PerformanceShowGrid(
                        performances = performances,
                        onPerformanceClick = { performance ->
                            selectedPerformance = performance
                        }
                    )
                }
            }
        }
    }

    selectedPerformance?.let { performance ->
        Dialog(
            onDismissRequest = { selectedPerformance = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                PerformanceFullContent(
                    performance = performance,
                    onClose = { selectedPerformance = null }
                )
            }
        }
    }
}
