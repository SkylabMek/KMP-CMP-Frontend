package th.skylabmek.kmp_frontend.features.feature.markdown.ui.components.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import org.jetbrains.compose.resources.stringResource
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.features.feature.markdown.model.MarkdownImage
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.image_picker_error
import th.skylabmek.kmp_frontend.shared_resources.image_picker_show_all_desc
import th.skylabmek.kmp_frontend.shared_resources.image_picker_title
import th.skylabmek.kmp_frontend.shared_resources.image_picker_upload_desc

@Composable
internal fun ImagePickerToolbar(
    imageState: UiState<List<UiState<MarkdownImage>>>,
    isUploading: Boolean,
    onUploadClick: () -> Unit,
    onShowAllClick: () -> Unit,
    onImageSelect: (MarkdownImage) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)) {
        Text(stringResource(Res.string.image_picker_title), style = MaterialTheme.typography.labelSmall)
        Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.CenterStart) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    Surface(
                        onClick = onShowAllClick,
                        modifier = Modifier.size(72.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        tonalElevation = 2.dp
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.GridView, 
                                contentDescription = stringResource(Res.string.image_picker_show_all_desc),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                item {
                    Card(
                        onClick = { if (!isUploading) onUploadClick() },
                        modifier = Modifier.size(72.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            if (isUploading) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = stringResource(Res.string.image_picker_upload_desc)
                                )
                            }
                        }
                    }
                }

                when (imageState) {
                    is UiState.Loading -> {
                        // Optional: Show some skeleton loading items
                    }
                    is UiState.Success -> {
                        val images = imageState.data
                        items(images) { state ->
                            if (state is UiState.Success) {
                                val image = state.data
                                Card(
                                    onClick = { onImageSelect(image) },
                                    modifier = Modifier.size(72.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    SubcomposeAsyncImage(
                                        model = image.imageUrl,
                                        contentDescription = image.filename,
                                        modifier = Modifier.fillMaxSize()
                                            .background(MaterialTheme.colorScheme.surface),
                                        contentScale = ContentScale.Crop,
                                        error = {
                                            Box(
                                                Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    Icons.Default.BrokenImage,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.error
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    is UiState.Error -> {
                        item {
                             Text(
                                 stringResource(Res.string.image_picker_error),
                                 color = MaterialTheme.colorScheme.error,
                                 style = MaterialTheme.typography.labelSmall
                             )
                        }
                    }
                }
            }
        }
    }
}
