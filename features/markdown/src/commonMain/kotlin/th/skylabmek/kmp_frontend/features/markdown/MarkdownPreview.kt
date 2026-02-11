package th.skylabmek.kmp_frontend.features.markdown

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

/**
 * A simple markdown preview component.
 * Currently it just displays raw text and images, but it's designed to be easily 
 * replaced with a full-featured markdown parser/renderer in the future.
 */
@Composable
fun MarkdownPreview(
    content: String,
    images: List<MarkdownImage> = emptyList(),
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(8.dp)
    ) {
        // Simple logic to split by image tags and show images
        // This is a basic placeholder for a real markdown parser
        val parts = content.split("(?=\\!\\[image\\]\\()|(?<=\\))".toRegex())
        
        parts.forEach { part ->
            if (part.startsWith("![image](")) {
                val imageId = part.substringAfter("![image](").substringBefore(")")
                val image = images.find { it.imageId == imageId }
                
                if (image != null) {
                    AsyncImage(
                        model = image.imageUrl,
                        contentDescription = image.filename,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = "Image not found: $imageId",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            } else if (part.isNotBlank()) {
                Text(
                    text = part,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
