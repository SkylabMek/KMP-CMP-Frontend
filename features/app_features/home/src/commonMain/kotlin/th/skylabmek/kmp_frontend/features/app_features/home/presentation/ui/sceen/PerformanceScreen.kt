package th.skylabmek.kmp_frontend.features.app_features.home.presentation.ui.sceen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.koin.compose.viewmodel.koinViewModel
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.domain.model.performances.Performance
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.components.performance.publicPerformance.PerformanceFullContent
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.components.performance.privatePerformance.PerformanceShowGrid
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.viewmodel.PerformanceListViewModel
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultErrorContent
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultLoadingContent
import th.skylabmek.kmp_frontend.ui.dimens.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceScreen(
    profileId: String,
    onBack: () -> Unit,
    viewModel: PerformanceListViewModel = koinViewModel(),
) {
    val performancesState by viewModel.performancesState.collectAsState()
    var selectedPerformance by remember { mutableStateOf<Performance?>(null) }

    LaunchedEffect(profileId) {
        viewModel.loadPerformances(profileId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.screenPadding)
    ) {
        when (val state = performancesState) {
            is UiState.Loading -> {
                DefaultLoadingContent(modifier = Modifier.fillMaxSize())
            }

            is UiState.Error -> {
                DefaultErrorContent(
                    error = state.uiError,
                    onRetry = { viewModel.loadPerformances(profileId) }
                )
            }

            is UiState.Success -> {
                val performances = state.data
                if (performances.isEmpty()) {
                    Text(
                        text = "No performances available.",
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
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.85f),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                PerformanceFullContent(
                    performance = performance,
                    onClose = { selectedPerformance = null }
                )
            }
        }
    }
}
