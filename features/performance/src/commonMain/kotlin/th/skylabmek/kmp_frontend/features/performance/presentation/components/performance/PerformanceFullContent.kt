package th.skylabmek.kmp_frontend.features.performance.presentation.components.performance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.features.markdown.ui.MarkdownView
import th.skylabmek.kmp_frontend.features.performance.presentation.viewmodel.PerformanceContentViewModel
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultErrorContent
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultLoadingContent
import th.skylabmek.kmp_frontend.ui.dimens.Dimens

@Composable
fun PerformanceFullContent(
    performanceContentViewModel: PerformanceContentViewModel = koinViewModel(),
    profileId: String,
    performanceId: String,
    modifier: Modifier = Modifier
) {
    val contentState by performanceContentViewModel.performanceContentState.collectAsState()

    LaunchedEffect(performanceId) {
        performanceContentViewModel.loadPerformanceContent(profileId, performanceId)
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = contentState) {
            is UiState.Loading -> {
                DefaultLoadingContent(modifier = Modifier.fillMaxSize())
            }
            is UiState.Error -> {
                DefaultErrorContent(
                    error = state.uiError,
                    onRetry = { performanceContentViewModel.loadPerformanceContent(profileId, performanceId) }
                )
            }
            is UiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(Dimens.paddingMedium)
                ) {
                    MarkdownView(
                        content = state.data.contentMarkdown,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
