package th.skylabmek.kmp_frontend.features.markdown

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun MarkdownEditor(
    value: String,
    onValueChange: (String) -> Unit,
    images: List<MarkdownImage> = emptyList(),
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember(value) {
        mutableStateOf(
            TextFieldValue(
                text = value,
                selection = TextRange(value.length)
            )
        )
    }

    MarkdownEditor(
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it
            if (value != it.text) {
                onValueChange(it.text)
            }
        },
        images = images,
        modifier = modifier
    )
}

@Composable
fun MarkdownEditor(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    images: List<MarkdownImage> = emptyList(),
    modifier: Modifier = Modifier
) {
    var isPreviewMode by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // Mode Selector & Toolbar
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Markdown Actions
            if (!isPreviewMode) {
                MarkdownToolbar(
                    value = value,
                    onValueChange = onValueChange
                )
            } else {
                Spacer(Modifier.weight(1f))
            }

            // Tabs
            Row {
                FilterChip(
                    selected = !isPreviewMode,
                    onClick = { isPreviewMode = false },
                    label = { Text("Edit") }
                )
                Spacer(Modifier.width(8.dp))
                FilterChip(
                    selected = isPreviewMode,
                    onClick = { isPreviewMode = true },
                    label = { Text("Preview") }
                )
            }
        }

        HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

        Box(modifier = Modifier.fillMaxSize()) {
            if (isPreviewMode) {
                MarkdownPreview(
                    content = value.text,
                    images = images,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Editor
                    OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(8.dp),
                        placeholder = { Text("Enter markdown here...") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    // Inline Image Preview (while editing)
                    val imagesInDoc = remember(value.text, images) {
                        images.filter { it.imageId in value.text }
                    }

                    if (imagesInDoc.isNotEmpty()) {
                        Text(
                            "Images in document:",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                                .padding(horizontal = 8.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(imagesInDoc) { image ->
                                Card(
                                    modifier = Modifier.padding(4.dp).fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        AsyncImage(
                                            model = image.imageUrl,
                                            contentDescription = image.filename,
                                            modifier = Modifier.size(60.dp),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Column {
                                            Text(image.filename, style = MaterialTheme.typography.bodySmall)
                                            Text("ID: ${image.imageId}", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MarkdownToolbar(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit
) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        IconButton(onClick = { onValueChange(applyMarkdownWrapper(value, "**")) }) {
            Icon(Icons.Default.FormatBold, contentDescription = "Bold")
        }
        IconButton(onClick = { onValueChange(applyMarkdownWrapper(value, "*")) }) {
            Icon(Icons.Default.FormatItalic, contentDescription = "Italic")
        }
        IconButton(onClick = { onValueChange(applyMarkdownHeader(value)) }) {
            Icon(Icons.Default.Title, contentDescription = "Heading")
        }
        IconButton(onClick = { onValueChange(applyMarkdownWrapper(value, "[", "](url)")) }) {
            Icon(Icons.Default.Link, contentDescription = "Link")
        }
        IconButton(onClick = { onValueChange(applyMarkdownList(value)) }) {
            Icon(Icons.Default.FormatListBulleted, contentDescription = "List")
        }
        IconButton(onClick = { onValueChange(applyMarkdownWrapper(value, "![image](", ")")) }) {
            Icon(Icons.Default.Image, contentDescription = "Image")
        }
    }
}

private fun applyMarkdownWrapper(
    value: TextFieldValue,
    prefix: String,
    suffix: String = prefix
): TextFieldValue {
    val selection = value.selection
    val text = value.text
    val selectedText = text.substring(selection.start, selection.end)
    
    val newText = text.replaceRange(selection.start, selection.end, "$prefix$selectedText$suffix")
    val newSelection = TextRange(
        start = selection.start + prefix.length,
        end = selection.end + prefix.length
    )
    
    return value.copy(text = newText, selection = newSelection)
}

private fun applyMarkdownHeader(value: TextFieldValue): TextFieldValue {
    val selection = value.selection
    val text = value.text
    
    val lineStart = text.lastIndexOf('\n', selection.start - 1).let { if (it == -1) 0 else it + 1 }
    val newText = text.replaceRange(lineStart, lineStart, "# ")
    
    return value.copy(
        text = newText,
        selection = TextRange(selection.start + 2, selection.end + 2)
    )
}

private fun applyMarkdownList(value: TextFieldValue): TextFieldValue {
    val selection = value.selection
    val text = value.text
    
    val lineStart = text.lastIndexOf('\n', selection.start - 1).let { if (it == -1) 0 else it + 1 }
    val newText = text.replaceRange(lineStart, lineStart, "- ")
    
    return value.copy(
        text = newText,
        selection = TextRange(selection.start + 2, selection.end + 2)
    )
}

@Preview
@Composable
fun MarkdownEditorPreview() {
    var text by remember { mutableStateOf("# Hello Preview\n\nTry using the **toolbar** buttons above!\n\n![image](img_001)") }
    val mockImages = listOf(
        MarkdownImage("img_001", "landscape.jpg", "https://picsum.photos/200/300")
    )
    
    Surface(color = MaterialTheme.colorScheme.background) {
        MarkdownEditor(
            value = text,
            onValueChange = { text = it },
            images = mockImages,
            modifier = Modifier.fillMaxSize()
        )
    }
}
