package th.skylabmek.kmp_frontend.features.feature.markdown.ui.components.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.coil3.Coil3ImageTransformerImpl
import com.mikepenz.markdown.m3.Markdown

@Composable
fun MarkdownView(
    content: String,
    modifier: Modifier = Modifier
) {
    Markdown(
        content = content,
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        // Use the official Coil3ImageTransformerImpl
        imageTransformer = Coil3ImageTransformerImpl
    )
}
