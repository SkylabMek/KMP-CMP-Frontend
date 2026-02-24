package th.skylabmek.kmp_frontend.features.feature.performance.presentation.components.performance.privatePerformance

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import th.skylabmek.kmp_frontend.core.common.UiError
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.core.common.asString
import th.skylabmek.kmp_frontend.domain.model.performances.Performance
import th.skylabmek.kmp_frontend.features.feature.markdown.ui.components.view.MarkdownPreview
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.viewmodel.PerformanceContentViewModel
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.viewmodel.PerformanceEditorViewModel
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.*
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultErrorContent
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultLoadingContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceFullContent(
    performance: Performance,
    profileId: String,
    onClose: () -> Unit,
    performanceContentViewModel: PerformanceContentViewModel = koinViewModel(),
    performanceEditorViewModel: PerformanceEditorViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val contentState by performanceContentViewModel.performanceContentState.collectAsState()
    val saveState by performanceEditorViewModel.saveState.collectAsState()
    val updateState by performanceContentViewModel.updateState.collectAsState()
    val deleteState by performanceContentViewModel.deleteState.collectAsState()
    
    var isEditingContent by remember { mutableStateOf(false) }
    var showInfoEditor by remember { mutableStateOf(false) }
    var editedContent by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(performance.id, performance.contentUrl) {
        performance.contentUrl?.let { url ->
            if (url.isNotBlank()) {
                performanceContentViewModel.loadPerformanceContent(url)
            }
        }
    }

    // Update editedContent when content is loaded
    LaunchedEffect(contentState) {
        if (contentState is UiState.Success) {
            editedContent = (contentState as UiState.Success).data.contentMarkdown
        }
    }

    // Handle content save success
    LaunchedEffect(saveState) {
        if (saveState is UiState.Success) {
            isEditingContent = false
            performanceEditorViewModel.resetSaveState()
            performance.contentUrl?.let { performanceContentViewModel.loadPerformanceContent(it) }
        }
    }

    // Handle info update success
    LaunchedEffect(updateState) {
        if (updateState is UiState.Success) {
            showInfoEditor = false
            performanceContentViewModel.resetUpdateState()
        }
    }

    // Handle delete success
    LaunchedEffect(deleteState) {
        if (deleteState is UiState.Success) {
            performanceContentViewModel.resetDeleteState()
            onClose()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditingContent) {
                            stringResource(Res.string.performance_detail_edit_content)
                        } else {
                            performance.title
                        },
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
                    if (isEditingContent) {
                        IconButton(
                            onClick = { 
                                scope.launch { 
                                    performanceEditorViewModel.saveContent(profileId, performance.id, editedContent) 
                                } 
                            },
                            enabled = saveState !is UiState.Loading
                        ) {
                            if (saveState is UiState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = stringResource(Res.string.dialog_save)
                                )
                            }
                        }
                    } else {
                        IconButton(onClick = { showInfoEditor = true }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = stringResource(Res.string.performance_detail_edit_info)
                            )
                        }
                        IconButton(onClick = { isEditingContent = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(Res.string.performance_detail_edit_content)
                            )
                        }
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
                        if (isEditingContent) {
                            PerformanceEditor(
                                profileId = profileId,
                                content = editedContent,
                                onContentChange = { editedContent = it },
                                viewModel = performanceEditorViewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            MarkdownPreview(
                                content = state.data.contentMarkdown,
                                modifier = Modifier.fillMaxSize().padding(16.dp)
                            )
                        }
                    }
                }
            }

            if (showInfoEditor) {
                PerformanceInfoEditorDialog(
                    performance = performance,
                    onDismissRequest = { showInfoEditor = false },
                    onSave = { request ->
                        performanceContentViewModel.updatePerformance(profileId, performance.id, request)
                    },
                    onDelete = {
                        performanceContentViewModel.deletePerformance(profileId, performance.id)
                    }
                )
            }

            // Error snackbars
            listOf(saveState, updateState, deleteState).filterIsInstance<UiState.Error>().firstOrNull()?.let { errorState ->
                val errorMsg = errorState.uiError.asString()
                Snackbar(
                    action = {
                        TextButton(onClick = { 
                            performanceEditorViewModel.resetSaveState()
                            performanceContentViewModel.resetUpdateState()
                            performanceContentViewModel.resetDeleteState()
                        }) {
                            Text(stringResource(Res.string.dialog_dismiss))
                        }
                    },
                    modifier = Modifier.padding(16.dp).align(Alignment.BottomCenter)
                ) {
                    Text(errorMsg)
                }
            }
            
            if (updateState is UiState.Loading || deleteState is UiState.Loading) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
