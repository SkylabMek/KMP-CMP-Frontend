package th.skylabmek.kmp_frontend.features.feature.performance.presentation.components.performance.publicPerformance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import th.skylabmek.kmp_frontend.core.common.UiError
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.domain.model.performances.Performance
import th.skylabmek.kmp_frontend.features.feature.markdown.ui.components.view.MarkdownView
import th.skylabmek.kmp_frontend.features.feature.performance.model.PerformanceCategoryUi
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.viewmodel.PerformanceContentViewModel
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.*
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultErrorContent
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultLoadingContent
import th.skylabmek.kmp_frontend.ui.dimens.Dimens

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
        // Single verticalScroll on the Column prevents infinite height measurement errors
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Enhanced Subtle Info Header
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.paddingMedium, vertical = Dimens.paddingSmall),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
            ) {
                // Category Tag
                PerformanceCategoryUi.fromId(performance.categoryId)?.let { cat ->
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text(
                            text = stringResource(cat.displayNameRes),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Status Badge (Closed/Active)
                if (performance.close) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Completed",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Date Range
                InfoItem(
                    icon = Icons.Default.DateRange,
                    text = buildString {
                        append(performance.startDate ?: "")
                        if (!performance.endDate.isNullOrBlank()) {
                            append(" - ")
                            append(performance.endDate)
                        }
                    }
                )

                // Location
                if (!performance.location.isNullOrBlank()) {
                    InfoItem(
                        icon = Icons.Default.LocationOn,
                        text = performance.location!!
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = Dimens.paddingMedium),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Content Area - No nested scrolling here
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.paddingMedium)
            ) {
                if (performance.contentUrl.isNullOrBlank()) {
                    DefaultErrorContent(
                        error = UiError.DynamicString("No content URL provided for this performance."),
                        onRetry = onClose
                    )
                } else {
                    when (val state = contentState) {
                        is UiState.Loading -> {
                            DefaultLoadingContent(modifier = Modifier.fillMaxWidth().height(200.dp))
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
                            // Using MarkdownView (non-scrollable) inside our scrollable Column
                            MarkdownView(
                                content = state.data.contentMarkdown,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
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

@Composable
private fun InfoItem(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
