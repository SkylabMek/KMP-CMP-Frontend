package th.skylabmek.kmp_frontend.features.feature.markdown.ui.components.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import org.jetbrains.compose.resources.stringResource
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.features.feature.markdown.model.MarkdownImage
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.dialog_close
import th.skylabmek.kmp_frontend.shared_resources.dialog_done
import th.skylabmek.kmp_frontend.shared_resources.dialog_performance_delete_confirm
import th.skylabmek.kmp_frontend.shared_resources.image_picker_delete_confirm_message
import th.skylabmek.kmp_frontend.shared_resources.image_picker_delete_confirm_title
import th.skylabmek.kmp_frontend.shared_resources.image_picker_loading_error
import th.skylabmek.kmp_frontend.shared_resources.image_picker_manage_title
import th.skylabmek.kmp_frontend.shared_resources.image_picker_no_images
import th.skylabmek.kmp_frontend.shared_resources.image_picker_toggle_edit_mode
import th.skylabmek.kmp_frontend.ui.components.dialog.ConfirmDialog

@Composable
internal fun ImageManagementDialog(
    imageState: UiState<List<UiState<MarkdownImage>>>,
    onDismissRequest: () -> Unit,
    onImageSelect: (MarkdownImage) -> Unit,
    onDeleteImage: (MarkdownImage) -> Unit,
    imageUsageInDoc: Map<String, Int> = emptyMap()
) {
    var isEditMode by remember { mutableStateOf(false) }
    var imageToDelete by remember { mutableStateOf<MarkdownImage?>(null) }

    imageToDelete?.let { image ->
        ConfirmDialog(
            title = stringResource(Res.string.image_picker_delete_confirm_title),
            message = stringResource(Res.string.image_picker_delete_confirm_message, image.filename),
            confirmText = stringResource(Res.string.dialog_performance_delete_confirm),
            isDangerous = true,
            onConfirm = { 
                onDeleteImage(image)
                imageToDelete = null
            },
            onDismiss = { imageToDelete = null }
        )
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(Res.string.image_picker_manage_title),
                    style = MaterialTheme.typography.titleLarge
                )
                Row {
                    IconButton(onClick = { isEditMode = !isEditMode }) {
                        Icon(
                            imageVector = if (isEditMode) Icons.Default.CheckCircle else Icons.Default.Edit,
                            contentDescription = stringResource(Res.string.image_picker_toggle_edit_mode),
                            tint = if (isEditMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onDismissRequest) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = stringResource(Res.string.dialog_close)
                        )
                    }
                }
            }
        },
        text = {
            Box(modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp, max = 500.dp)) {
                when (imageState) {
                    is UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    is UiState.Error -> Text(
                        stringResource(Res.string.image_picker_loading_error),
                        color = MaterialTheme.colorScheme.error
                    )
                    is UiState.Success -> {
                        val images = imageState.data.filterIsInstance<UiState.Success<MarkdownImage>>()
                        if (images.isEmpty()) {
                            Text(
                                stringResource(Res.string.image_picker_no_images),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(images) { state ->
                                    val image = state.data
                                    Box {
                                        Card(
                                            onClick = { if (!isEditMode) onImageSelect(image) },
                                            modifier = Modifier.aspectRatio(1f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            SubcomposeAsyncImage(
                                                model = image.imageUrl,
                                                contentDescription = image.filename,
                                                modifier = Modifier.fillMaxSize()
                                                    .background(MaterialTheme.colorScheme.surface),
                                                contentScale = ContentScale.Crop,
                                                error = {
                                                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                        Icon(Icons.Default.BrokenImage, contentDescription = null)
                                                    }
                                                }
                                            )
                                        }

                                        // Usage Count Badge (Bottom-Right) - Show count in current document
                                        val countInDoc = imageUsageInDoc[image.imageUrl] ?: 0
                                        if (countInDoc > 0) {
                                            Surface(
                                                modifier = Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .padding(4.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = countInDoc.toString(),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        // Change indicator (-N) (Top-Right) - Only show if usage decreased
                                        val diff = countInDoc - image.usageCount
                                        if (diff < 0 && !isEditMode) {
                                            Surface(
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(4.dp),
                                                color = MaterialTheme.colorScheme.errorContainer,
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = diff.toString(),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp),
                                                    color = MaterialTheme.colorScheme.onErrorContainer
                                                )
                                            }
                                        }

                                        if (isEditMode) {
                                            IconButton(
                                                onClick = { imageToDelete = image },
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(4.dp)
                                                    .size(24.dp)
                                                    .background(MaterialTheme.colorScheme.error, CircleShape)
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = stringResource(Res.string.dialog_performance_delete_confirm),
                                                    tint = MaterialTheme.colorScheme.onError,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (isEditMode) {
                TextButton(onClick = { isEditMode = false }) {
                    Text(stringResource(Res.string.dialog_done))
                }
            }
        }
    )
}
