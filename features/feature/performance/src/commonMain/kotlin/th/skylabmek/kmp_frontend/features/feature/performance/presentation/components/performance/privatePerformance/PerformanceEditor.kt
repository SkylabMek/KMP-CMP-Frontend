package th.skylabmek.kmp_frontend.features.feature.performance.presentation.components.performance.privatePerformance

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import th.skylabmek.kmp_frontend.features.feature.markdown.ui.components.editor.MarkdownEditor
import th.skylabmek.kmp_frontend.features.feature.markdown.ui.components.view.MarkdownPreview
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.viewmodel.PerformanceEditorViewModel
import th.skylabmek.kmp_frontend.features.tools.patfrom_features.implementation.PlatformFilePickerHandler
import th.skylabmek.kmp_frontend.features.tools.patfrom_features.implementation.rememberPlatformFilePicker
import th.skylabmek.kmp_frontend.ui.config.UI

@Composable
fun PerformanceEditor(
    profileId: String,
    content: String,
    onContentChange: (String) -> Unit,
    viewModel: PerformanceEditorViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val isDesktop = UI.isDesktop
    var mobileSelectedTabIndex by remember { mutableIntStateOf(0) }
    val imageState by viewModel.imageState.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()
    val scope = rememberCoroutineScope()
    
    val filePicker = rememberPlatformFilePicker()
    PlatformFilePickerHandler(filePicker)

    LaunchedEffect(profileId) {
        viewModel.loadImages(profileId)
    }

    val onUploadClick = {
        scope.launch {
            val file = filePicker.pickImage()
            if (file != null) {
                viewModel.uploadImage(
                    profileId = profileId,
                    fileBytes = file.bytes,
                    fileName = file.name,
                    mimeType = file.mimeType
                )
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (isDesktop) {
            Row(modifier = Modifier.fillMaxSize()) {
                MarkdownEditor(
                    value = content,
                    onValueChange = onContentChange,
                    imageState = imageState,
                    isUploading = isUploading,
                    onUploadClick = { onUploadClick() },
                    onDeleteImage = { image ->
                        viewModel.deleteImage(profileId, image.imageId)
                    },
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                
                VerticalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                
                MarkdownPreview(
                    content = content,
                    modifier = Modifier.weight(1f).fillMaxHeight().padding(16.dp)
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    FilterChip(
                        selected = mobileSelectedTabIndex == 1,
                        onClick = { mobileSelectedTabIndex = if (mobileSelectedTabIndex == 0) 1 else 0 },
                        label = { 
                            Text(if (mobileSelectedTabIndex == 0) "Preview" else "Edit") 
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (mobileSelectedTabIndex == 0) Icons.Default.Visibility else Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }

                Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                    if (mobileSelectedTabIndex == 1) {
                        MarkdownPreview(
                            content = content,
                            modifier = Modifier.fillMaxSize().padding(16.dp)
                        )
                    } else {
                        MarkdownEditor(
                            value = content,
                            onValueChange = onContentChange,
                            imageState = imageState,
                            isUploading = isUploading,
                            onUploadClick = { onUploadClick() },
                            onDeleteImage = { image ->
                                viewModel.deleteImage(profileId, image.imageId)
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
