package th.skylabmek.kmp_frontend.features.markdown.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mikepenz.markdown.m3.Markdown

@Composable
fun MarkdownView(
    content: String,
    modifier: Modifier = Modifier
) {
    Markdown(
        content = content,
        modifier = modifier.fillMaxWidth()
    )
}
