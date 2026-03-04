package th.skylabmek.kmp_frontend.features.feature.markdown.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.features.feature.markdown.model.MarkdownImage
import th.skylabmek.kmp_frontend.features.feature.markdown.ui.components.editor.*
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.*
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
                    val imagesInDoc = remember(value.text, imageState.data) {
                        imageState.data.filterIsInstance<UiState.Success<MarkdownImage>>()
                            .map { it.data }
                            .filter { it.imageUrl in value.text }
                    }
                    
                    DocumentImageSection(
                        images = imagesInDoc,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                is UiState.Error -> Unit // Fail silently for inline images
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Extension helpers
// ---------------------------------------------------------------------------

private fun TextRange.coerceIn(min: Int, max: Int) =
    TextRange(start.coerceIn(min, max), end.coerceIn(min, max))
