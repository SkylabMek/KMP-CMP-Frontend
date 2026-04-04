package th.skylabmek.kmp_frontend.features.app_features.profile.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.core.common.util.IconUrlBuilder
import th.skylabmek.kmp_frontend.domain.model.app.AppConfig
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.ic_profile
import th.skylabmek.kmp_frontend.ui.config.LocalAppConfig

@Composable
fun BrandIcon(
    logoUrl: String?,
    domain: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    padding: Dp = 0.dp, // Default to 0 so the image can fill the circle
    fallbackIcon: ImageVector = Icons.Default.Link,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
    iconColor: Color = MaterialTheme.colorScheme.primary
) {
    val appConfigState = LocalAppConfig.current
    val appConfig = if (appConfigState is UiState.Success<AppConfig>) appConfigState.data else null

    val iconUrl = run {
        // 1. If logoUrl is a full URL, use it directly.
        if (logoUrl != null && (logoUrl.startsWith("http://") || logoUrl.startsWith("https://"))) {
            return@run logoUrl
        }

        // 2. Try to use Brandfetch if we have a domain (either from logoUrl or domain param)
        if (appConfig != null) {
            val domainToUse = when {
                // If logoUrl looks like a domain (contains dot but not http), use it.
                logoUrl != null && !logoUrl.startsWith("http") && logoUrl.contains(".") -> logoUrl
                // Otherwise use the provided domain parameter.
                domain != null && domain.contains(".") -> domain
                else -> null
            }

            if (domainToUse != null) {
                return@run IconUrlBuilder.buildBrandfetchUrl(
                    iconBaseUrl = appConfig.iconBaseUrl,
                    domain = domainToUse
                )
            }
        }

        null
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        if (iconUrl != null) {
            AsyncImage(
                model = iconUrl,
                contentDescription = contentDescription,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape), // Clip the image itself to remove square corners
                contentScale = ContentScale.Crop, // Crop to fill the circular container
                error = painterResource(Res.drawable.ic_profile),
                placeholder = painterResource(Res.drawable.ic_profile)
            )
        } else {
            Icon(
                imageVector = fallbackIcon,
                contentDescription = contentDescription,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(size * 0.2f), // Add internal padding only for vector icons
                tint = iconColor
            )
        }
    }
}
