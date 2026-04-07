package th.skylabmek.kmp_frontend.features.feature.markdown.ui.components.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import org.jetbrains.compose.resources.stringResource
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.features.feature.markdown.model.MarkdownImage
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.markdown_editor_images_in_doc
import th.skylabmek.kmp_frontend.shared_resources.markdown_editor_placeholder
import th.skylabmek.kmp_frontend.shared_resources.markdown_editor_url_label
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultLoadingContent

@Composable
fun MarkdownEditor(
    value: String,
    onValueChange: (String) -> Unit,
    imageState: UiState<List<UiState<MarkdownImage>>> = UiState.Success(emptyList()),
    onUploadClick: () -> Unit = {},
    onDeleteImage: (MarkdownImage) -> Unit = {},
    isUploading: Boolean = false,
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
    }

    LaunchedEffect(value) {
        if (value != textFieldValue.text) {
            textFieldValue = textFieldValue.copy(
                text = value,
                selection = textFieldValue.selection.coerceIn(0, value.length)
            )
        }
    }

    MarkdownEditor(
        value = textFieldValue,
        onValueChange = { next ->
            val prevText = textFieldValue.text
            textFieldValue = next
            if (prevText != next.text) onValueChange(next.text)
        },
        imageState = imageState,
        onUploadClick = onUploadClick,
        onDeleteImage = onDeleteImage,
        isUploading = isUploading,
        modifier = modifier
    )
}

@Composable
fun MarkdownEditor(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    imageState: UiState<List<UiState<MarkdownImage>>> = UiState.Success(emptyList()),
    onUploadClick: () -> Unit = {},
    onDeleteImage: (MarkdownImage) -> Unit = {},
    isUploading: Boolean = false,
    modifier: Modifier = Modifier
) {
    var showImagePicker by remember { mutableStateOf(false) }
    var showFullImagePicker by remember { mutableStateOf(false) }

    val handleCommand = { command: MarkdownCommand ->
        onValueChange(command.execute(value))
    }

    val imageUsageInDoc = remember(value.text, imageState) {
        if (imageState is UiState.Success) {
            imageState.data.filterIsInstance<UiState.Success<MarkdownImage>>()
                .associate { state ->
                    val image = state.data
                    val count = value.text.split(image.imageUrl).size - 1
                    image.imageUrl to count
                }
        } else {
            emptyMap()
        }
    }

    Column(modifier = modifier) {
        MarkdownToolbar(
            onCommand = handleCommand,
            onImageClick = { showImagePicker = !showImagePicker }
        )

        if (showImagePicker) {
            ImagePickerToolbar(
                imageState = imageState,
                isUploading = isUploading,
                onUploadClick = onUploadClick,
                onShowAllClick = { showFullImagePicker = true },
                onImageSelect = { image ->
                    handleCommand(MarkdownCommands.image(image.imageUrl))
                    showImagePicker = false
                },
                imageUsageInDoc = imageUsageInDoc
            )
        }

        if (showFullImagePicker) {
            ImageManagementDialog(
                imageState = imageState,
                onDismissRequest = { showFullImagePicker = false },
                onImageSelect = { image ->
                    handleCommand(MarkdownCommands.image(image.imageUrl))
                    showFullImagePicker = false
                    showImagePicker = false
                },
                onDeleteImage = onDeleteImage,
                imageUsageInDoc = imageUsageInDoc
            )
        }

        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

        Column(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth().weight(1f).padding(8.dp),
                placeholder = { Text(stringResource(Res.string.markdown_editor_placeholder)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            when (imageState) {
                is UiState.Loading -> DefaultLoadingContent(
                    modifier = Modifier.fillMaxWidth().height(100.dp)
                )

                is UiState.Success -> {
                    val imagesInDoc = remember(value.text, imageState.data, imageUsageInDoc) {
                        imageState.data.filterIsInstance<UiState.Success<MarkdownImage>>()
                            .mapNotNull { state ->
                                val image = state.data
                                val countInDoc = imageUsageInDoc[image.imageUrl] ?: 0
                                if (countInDoc > 0) image to countInDoc else null
                            }
                    }
                    if (imagesInDoc.isNotEmpty()) {
                        Text(
                            stringResource(Res.string.markdown_editor_images_in_doc),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)
                                .padding(horizontal = 8.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(imagesInDoc) { (image, countInDoc) ->
                                Card(
                                    modifier = Modifier.padding(4.dp).fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                                            alpha = 0.5f
                                        )
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        Box(modifier = Modifier.size(60.dp)) {
                                            SubcomposeAsyncImage(
                                                model = image.imageUrl,
                                                contentDescription = image.filename,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop,
                                                error = {
                                                    Icon(
                                                        Icons.Default.BrokenImage,
                                                        contentDescription = null,
                                                        tint = MaterialTheme.colorScheme.error,
                                                        modifier = Modifier.fillMaxSize().padding(8.dp)
                                                    )
                                                }
                                            )

                                            // Usage Count Badge (Bottom-Right) - Show count in current document
                                            if (countInDoc > 0) {
                                                Surface(
                                                    modifier = Modifier
                                                        .align(Alignment.BottomEnd)
                                                        .padding(2.dp),
                                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = countInDoc.toString(),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp),
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }

                                            // Change indicator (+N/-N) (Top-Right)
                                            val diff = countInDoc - image.usageCount
                                            if (diff != 0) {
                                                Surface(
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .padding(2.dp),
                                                    color = if (diff > 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = if (diff > 0) "+$diff" else diff.toString(),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp),
                                                        color = if (diff > 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                image.filename,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            Text(
                                                "URL: ${image.imageUrl}",
                                                style = MaterialTheme.typography.labelSmall,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        
                                        // Doc usage count
                                        Surface(
                                            color = MaterialTheme.colorScheme.secondaryContainer,
                                            shape = CircleShape,
                                            modifier = Modifier.padding(start = 8.dp)
                                        ) {
                                            Text(
                                                text = countInDoc.toString(),
                                                style = MaterialTheme.typography.labelMedium,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                is UiState.Error -> Unit // Fail silently for inline images
            }
        }
    }
}

private fun TextRange.coerceIn(min: Int, max: Int) =
    TextRange(start.coerceIn(min, max), end.coerceIn(min, max))
