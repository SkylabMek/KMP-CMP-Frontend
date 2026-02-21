package th.skylabmek.kmp_frontend.features.markdown.ui.components.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.features.markdown.model.MarkdownImage
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
                }
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
                onDeleteImage = onDeleteImage
            )
        }

        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

        Column(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth().weight(1f).padding(8.dp),
                placeholder = { Text("Enter markdown here...") },
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
                    val imagesInDoc = remember(value.text, imageState.data) {
                        imageState.data.filterIsInstance<UiState.Success<MarkdownImage>>()
                            .filter { it.data.imageUrl in value.text }
                    }
                    if (imagesInDoc.isNotEmpty()) {
                        Text(
                            "Images in document:",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)
                                .padding(horizontal = 8.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(imagesInDoc) { state ->
                                val image = state.data
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
                                        SubcomposeAsyncImage(
                                            model = image.imageUrl,
                                            contentDescription = image.filename,
                                            modifier = Modifier.size(60.dp),
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
                                        Spacer(Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                image.filename,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                            Text(
                                                "URL: ${image.imageUrl}",
                                                style = MaterialTheme.typography.labelSmall,
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
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
