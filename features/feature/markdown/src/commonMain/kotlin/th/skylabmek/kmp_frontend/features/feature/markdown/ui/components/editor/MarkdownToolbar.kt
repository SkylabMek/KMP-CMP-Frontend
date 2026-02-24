package th.skylabmek.kmp_frontend.features.feature.markdown.ui.components.editor

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
import org.jetbrains.compose.resources.stringResource
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.markdown_toolbar_bold
import th.skylabmek.kmp_frontend.shared_resources.markdown_toolbar_heading
import th.skylabmek.kmp_frontend.shared_resources.markdown_toolbar_image
import th.skylabmek.kmp_frontend.shared_resources.markdown_toolbar_italic
import th.skylabmek.kmp_frontend.shared_resources.markdown_toolbar_link
import th.skylabmek.kmp_frontend.shared_resources.markdown_toolbar_list

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
            Icon(Icons.Default.FormatBold, contentDescription = stringResource(Res.string.markdown_toolbar_bold))
        }
        IconButton(onClick = { onCommand(MarkdownCommands.italic()) }) {
            Icon(Icons.Default.FormatItalic, contentDescription = stringResource(Res.string.markdown_toolbar_italic))
        }
        IconButton(onClick = { onCommand(MarkdownCommands.heading()) }) {
            Icon(Icons.Default.Title, contentDescription = stringResource(Res.string.markdown_toolbar_heading))
        }
        IconButton(onClick = { onCommand(MarkdownCommands.link()) }) {
            Icon(Icons.Default.Link, contentDescription = stringResource(Res.string.markdown_toolbar_link))
        }
        IconButton(onClick = { onCommand(MarkdownCommands.list()) }) {
            Icon(Icons.AutoMirrored.Filled.FormatListBulleted, contentDescription = stringResource(Res.string.markdown_toolbar_list))
        }
        IconButton(onClick = onImageClick) {
            Icon(Icons.Default.Image, contentDescription = stringResource(Res.string.markdown_toolbar_image))
        }
    }
}
