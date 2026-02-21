package th.skylabmek.kmp_frontend.features.feature.markdown.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.features.feature.markdown.model.MarkdownImage
import th.skylabmek.kmp_frontend.features.feature.markdown.ui.MarkdownView

/**
 * A simple markdown preview component that delegates to MarkdownView and provides vertical scrolling.
 */
@Composable
fun MarkdownPreview(
    content: String,
    imageState: UiState<List<UiState<MarkdownImage>>> = UiState.Success(emptyList()),
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
