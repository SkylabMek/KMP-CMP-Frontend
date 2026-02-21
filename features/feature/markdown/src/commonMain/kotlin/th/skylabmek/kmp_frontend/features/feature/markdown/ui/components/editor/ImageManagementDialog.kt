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
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.features.feature.markdown.model.MarkdownImage
import th.skylabmek.kmp_frontend.ui.components.dialog.ConfirmDialog

@Composable
internal fun ImageManagementDialog(
    imageState: UiState<List<UiState<MarkdownImage>>>,
    onDismissRequest: () -> Unit,
    onImageSelect: (MarkdownImage) -> Unit,
    onDeleteImage: (MarkdownImage) -> Unit
) {
    var isEditMode by remember { mutableStateOf(false) }
    var imageToDelete by remember { mutableStateOf<MarkdownImage?>(null) }

    imageToDelete?.let { image ->
        ConfirmDialog(
            title = "Delete Image",
            message = "Are you sure you want to delete '${image.filename}'? This action cannot be undone.",
            confirmText = "Delete",
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
                Text("Manage Images", style = MaterialTheme.typography.titleLarge)
                Row {
                    IconButton(onClick = { isEditMode = !isEditMode }) {
                        Icon(
                            imageVector = if (isEditMode) Icons.Default.CheckCircle else Icons.Default.Edit,
                            contentDescription = "Toggle Edit Mode",
                            tint = if (isEditMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
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
                    is UiState.Error -> Text("Error loading images", color = MaterialTheme.colorScheme.error)
                    is UiState.Success -> {
                        val images = imageState.data.filterIsInstance<UiState.Success<MarkdownImage>>()
                        if (images.isEmpty()) {
                            Text("No images uploaded yet.", modifier = Modifier.align(Alignment.Center))
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
                                                    contentDescription = "Delete",
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
                    Text("Done")
                }
            }
        }
    )
}
