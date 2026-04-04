package th.skylabmek.kmp_frontend.features.app_features.profile.components.social

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import th.skylabmek.kmp_frontend.domain.model.profile.Social
import th.skylabmek.kmp_frontend.features.app_features.profile.components.common.BrandIcon

@Composable
fun SocialIcon(
    social: Social,
    uriHandler: UriHandler,
    modifier: Modifier = Modifier
) {
    val domain = social.link
        .removePrefix("https://")
        .removePrefix("http://")
        .removePrefix("www.")
        .split("/")
        .firstOrNull() ?: ""

    Row(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable {
                if (social.link.isNotEmpty()) {
                    try {
                        uriHandler.openUri(social.link)
                    } catch (e: Exception) {
                        // Handle invalid URI
                    }
                }
            }
            .padding(end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BrandIcon(
            logoUrl = social.logoUrl,
            domain = domain,
            contentDescription = social.name,
            size = 40.dp,
            padding = 8.dp
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = social.name,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
