package th.skylabmek.kmp_frontend.features.performance.presentation.components.performance

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
import org.koin.compose.viewmodel.koinViewModel
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.core.common.errorMessage
import th.skylabmek.kmp_frontend.features.markdown.ui.components.MarkdownPreview
import th.skylabmek.kmp_frontend.features.performance.presentation.viewmodel.PerformanceContentViewModel
import th.skylabmek.kmp_frontend.features.performance.presentation.viewmodel.PerformanceEditorViewModel
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultErrorContent
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultLoadingContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceFullContent(
    performanceContentViewModel: PerformanceContentViewModel = koinViewModel(),
    performanceEditorViewModel: PerformanceEditorViewModel = koinViewModel(),
    profileId: String,
    performanceId: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentState by performanceContentViewModel.performanceContentState.collectAsState()
    val performanceState by performanceContentViewModel.performanceState.collectAsState()
    val saveState by performanceEditorViewModel.saveState.collectAsState()
    val updateState by performanceContentViewModel.updateState.collectAsState()
    
    var isEditingContent by remember { mutableStateOf(false) }
    var showInfoEditor by remember { mutableStateOf(false) }
    var editedContent by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(performanceId) {
        performanceContentViewModel.loadPerformanceContent(profileId, performanceId)
        performanceContentViewModel.loadPerformance(profileId, performanceId)
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
            performanceContentViewModel.loadPerformanceContent(profileId, performanceId)
            performanceEditorViewModel.resetSaveState()
        }
    }

    // Handle info update success
    LaunchedEffect(updateState) {
        if (updateState is UiState.Success) {
            showInfoEditor = false
            performanceContentViewModel.resetUpdateState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditingContent) "Edit Content" else "Performance Detail",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    if (isEditingContent) {
                        IconButton(
                            onClick = { 
                                scope.launch { 
                                    performanceEditorViewModel.saveContent(profileId, performanceId, editedContent) 
                                } 
                            },
                            enabled = saveState !is UiState.Loading
                        ) {
                            if (saveState is UiState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
                            }
                        }
                    } else {
                        // Icon to edit performance info
                        IconButton(onClick = { showInfoEditor = true }) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = "Edit Info")
                        }
                        // Icon to toggle content editing
                        IconButton(onClick = { isEditingContent = true }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Content")
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

            // Info Editor Dialog
            if (showInfoEditor && performanceState is UiState.Success) {
                PerformanceInfoEditorDialog(
                    performance = (performanceState as UiState.Success).data,
                    onDismissRequest = { showInfoEditor = false },
                    onSave = { request ->
                        performanceContentViewModel.updatePerformance(profileId, performanceId, request)
                    }
                )
            }

            // Error snackbar for save content
            if (saveState is UiState.Error) {
                val errorMsg = (saveState as UiState.Error).uiError.errorMessage().invoke()
                Snackbar(
                    action = {
                        TextButton(onClick = { performanceEditorViewModel.resetSaveState() }) {
                            Text("Dismiss")
                        }
                    },
                    modifier = Modifier.padding(16.dp).align(Alignment.BottomCenter)
                ) {
                    Text(errorMsg)
                }
            }

            // Error snackbar for update info
            if (updateState is UiState.Error) {
                val errorMsg = (updateState as UiState.Error).uiError.errorMessage().invoke()
                Snackbar(
                    action = {
                        TextButton(onClick = { performanceContentViewModel.resetUpdateState() }) {
                            Text("Dismiss")
                        }
                    },
                    modifier = Modifier.padding(16.dp).align(Alignment.BottomCenter)
                ) {
                    Text(errorMsg)
                }
            }
            
            // Loading overlay for info update
            if (updateState is UiState.Loading) {
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
