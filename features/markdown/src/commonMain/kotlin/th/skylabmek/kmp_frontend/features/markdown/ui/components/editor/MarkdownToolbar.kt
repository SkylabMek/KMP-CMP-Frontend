package th.skylabmek.kmp_frontend.features.markdown.ui.components.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun MarkdownToolbar(
    onCommand: (MarkdownCommand) -> Unit,
    onImageClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        IconButton(onClick = { onCommand(MarkdownCommands.bold()) }) {
            Icon(Icons.Default.FormatBold, contentDescription = "Bold")
        }
        IconButton(onClick = { onCommand(MarkdownCommands.italic()) }) {
            Icon(Icons.Default.FormatItalic, contentDescription = "Italic")
        }
        IconButton(onClick = { onCommand(MarkdownCommands.heading()) }) {
            Icon(Icons.Default.Title, contentDescription = "Heading")
        }
        IconButton(onClick = { onCommand(MarkdownCommands.link()) }) {
            Icon(Icons.Default.Link, contentDescription = "Link")
        }
        IconButton(onClick = { onCommand(MarkdownCommands.list()) }) {
            Icon(Icons.AutoMirrored.Filled.FormatListBulleted, contentDescription = "List")
        }
        IconButton(onClick = onImageClick) {
            Icon(Icons.Default.Image, contentDescription = "Image")
        }
    }
}
