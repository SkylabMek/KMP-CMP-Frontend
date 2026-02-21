package th.skylabmek.kmp_frontend.features.markdown.ui.components.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A simple markdown preview component that delegates to MarkdownView and provides vertical scrolling.
 */
@Composable
fun MarkdownPreview(
    content: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        MarkdownView(
            content = content,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
