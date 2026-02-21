package th.skylabmek.kmp_frontend.uiComponents.layout.header

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.domain.model.profile.LifeStatus
import th.skylabmek.kmp_frontend.features.app_features.profile.components.lifeStatus.LifeStatusIndicatorWithTooltip
import th.skylabmek.kmp_frontend.navigation.tools.NavigatorAccessor
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.header_menu_icon_desc
import th.skylabmek.kmp_frontend.shared_resources.header_site_name
import th.skylabmek.kmp_frontend.ui.components.button.ThemeToggle
import th.skylabmek.kmp_frontend.ui.components.card.appCard.AppElevatedCard
import th.skylabmek.kmp_frontend.ui.navigation.NavItemIcon
import th.skylabmek.kmp_frontend.ui.config.ThemeSetting
import th.skylabmek.kmp_frontend.ui.config.UI

@Composable
fun AppHeader(
    modifier: Modifier = Modifier,
    lifeStatusState: UiState<LifeStatus>,
    navigationItems: List<NavItemIcon?> = emptyList(),
    themeSetting: ThemeSetting = UI.themeSetting,
    onThemeToggle: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    AppElevatedCard(
        modifier = modifier
    ) {
        if (UI.isDesktop) {
            DesktopHeaderContent(
                lifeStatusState = lifeStatusState,
                navigationItems = navigationItems,
                themeSetting = themeSetting,
                onThemeToggle = onThemeToggle,
                onMenuClick = onMenuClick
            )
        } else {
            MobileHeaderContent(
                lifeStatusState = lifeStatusState,
                themeSetting = themeSetting,
                onThemeToggle = onThemeToggle,
                onMenuClick = onMenuClick
            )
        }
    }
}

@Composable
private fun DesktopHeaderContent(
    lifeStatusState: UiState<LifeStatus>,
    navigationItems: List<NavItemIcon?>,
    themeSetting: ThemeSetting,
    onThemeToggle: () -> Unit,
    onMenuClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = UI.dimens.paddingMedium, vertical = UI.dimens.paddingSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Logo + Name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UI.dimens.spaceMedium)
        ) {
            HeaderLogo()
            Text(
                text = stringResource(Res.string.header_site_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            LifeStatusIndicatorWithTooltip(uiState = lifeStatusState)
        }

        // Center: Navigation
        NavigationLinks(
            navigationItems = navigationItems,
            modifier = Modifier.weight(1f)
        )

        // Right side: Theme Toggle + Menu
        Row(
            horizontalArrangement = Arrangement.spacedBy(UI.dimens.spaceSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThemeToggle()

            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(Res.string.header_menu_icon_desc)
                )
            }
        }
    }
}

@Composable
private fun MobileHeaderContent(
    lifeStatusState: UiState<LifeStatus>,
    themeSetting: ThemeSetting,
    onThemeToggle: () -> Unit,
    onMenuClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = UI.dimens.paddingMedium, vertical = UI.dimens.paddingSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Logo + Name
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UI.dimens.spaceSmall)
        ) {
            HeaderLogo(size = 32.dp)
            Text(
                text = stringResource(Res.string.header_site_name),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            LifeStatusIndicatorWithTooltip(uiState = lifeStatusState)
        }

        // Right side: Theme Toggle + Menu
        Row(
            horizontalArrangement = Arrangement.spacedBy(UI.dimens.spaceExtraSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThemeToggle()

            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(Res.string.header_menu_icon_desc)
                )
            }
        }
    }
}

@Composable
private fun NavigationLinks(
    navigationItems: List<NavItemIcon?>,
    modifier: Modifier = Modifier
) {
    val navigator = NavigatorAccessor.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        navigationItems.forEach { item ->
            if (item != null) {
                NavigationItem(
                    text = item.title,
                    isActive = false,
                    onClick = { item.key?.let { navigator.navigate(it) } }
                )
            } else {
                // Blurred "Coming Soon" placeholder for null elements
                NavigationItem(
                    text = "Soon",
                    isActive = false,
                    isComingSoon = true,
                    onClick = { }
                )
            }
        }
    }
}

@Composable
private fun NavigationItem(
    text: String,
    isActive: Boolean,
    isComingSoon: Boolean = false,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        enabled = !isComingSoon,
        modifier = if (isComingSoon) Modifier.blur(4.dp) else Modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    isActive -> MaterialTheme.colorScheme.primary
                    isComingSoon -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    else -> MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.alpha(if (isComingSoon) 0.5f else 1f)
            )

            // Underline for active item
            if (isActive) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(2.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.shapes.small
                        )
                )
            }
        }
    }
}
