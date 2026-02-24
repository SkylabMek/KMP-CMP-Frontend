package th.skylabmek.kmp_frontend.features.feature.performance.presentation.components.performance.publicPerformance

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import th.skylabmek.kmp_frontend.core.common.UiError
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.domain.model.performances.Performance
import th.skylabmek.kmp_frontend.features.feature.markdown.ui.components.view.MarkdownPreview
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.viewmodel.PerformanceContentViewModel

import th.skylabmek.kmp_frontend.ui.components.layout.DefaultErrorContent
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultLoadingContent

import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceFullContent(
    performance: Performance,
    onClose: () -> Unit,
    performanceContentViewModel: PerformanceContentViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val contentState by performanceContentViewModel.performanceContentState.collectAsState()
    var showInfoDialog by remember { mutableStateOf(false) }

    LaunchedEffect(performance.id, performance.contentUrl) {
        performance.contentUrl?.let { url ->
            if (url.isNotBlank()) {
                performanceContentViewModel.loadPerformanceContent(url)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = performance.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(Res.string.dialog_close)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = stringResource(Res.string.performance_detail_edit_info)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (performance.contentUrl.isNullOrBlank()) {
                DefaultErrorContent(
                    error = UiError.DynamicString("No content URL provided for this performance."),
                    onRetry = onClose
                )
            } else {
                when (val state = contentState) {
                    is UiState.Loading -> {
                        DefaultLoadingContent(modifier = Modifier.fillMaxSize())
                    }
                    is UiState.Error -> {
                        DefaultErrorContent(
                            error = state.uiError,
                            onRetry = {
                                performance.contentUrl?.let { performanceContentViewModel.loadPerformanceContent(it) }
                            }
                        )
                    }
                    is UiState.Success -> {
                        MarkdownPreview(
                            content = state.data.contentMarkdown,
                            modifier = Modifier.fillMaxSize().padding(16.dp)
                        )
                    }
                }
            }

            if (showInfoDialog) {
                PerformanceInfoDialog(
                    performance = performance,
                    onDismissRequest = { showInfoDialog = false }
                )
            }
        }
    }
}
