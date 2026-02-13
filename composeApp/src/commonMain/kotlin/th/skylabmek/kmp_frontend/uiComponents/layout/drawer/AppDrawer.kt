package th.skylabmek.kmp_frontend.uiComponents.layout.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import th.skylabmek.kmp_frontend.features.profile.navigation.ProfileNavKey
import th.skylabmek.kmp_frontend.navigation.tools.NavigatorAccessor
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.*
import th.skylabmek.kmp_frontend.ui.navigation.NavItemIcon

/**
 * A reusable Modal Navigation Drawer for the application.
 *
 * @param drawerState The state of the drawer.
 * @param navigationItems List of items to display in the drawer.
 * @param content The content to be displayed behind the drawer.
 */
@Composable
fun AppDrawer(
    drawerState: DrawerState,
    navigationItems: List<NavItemIcon?>,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val navigator = NavigatorAccessor.current

    // To make the drawer slide from the right, we wrap the drawer in RTL LayoutDirection.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                // We wrap the drawer content back in LTR so the text and UI inside the drawer look normal.
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    ModalDrawerSheet(
                        // Since the drawer is on the right, we round the LEFT corners (start side in LTR).
                        drawerShape = RoundedCornerShape(
                            topStart = 16.dp, 
                            bottomStart = 16.dp, 
                            topEnd = 0.dp, 
                            bottomEnd = 0.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = stringResource(Res.string.nav_menu),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )

                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))

                            navigationItems.forEach { item ->
                                if (item != null) {
                                    NavigationDrawerItem(
                                        label = { Text(item.title) },
                                        selected = false,
                                        onClick = {
                                            scope.launch { drawerState.close() }
                                            item.key?.let { navigator.navigate(it) }
                                        },
                                        icon = item.icon?.let {
                                            {
                                                Icon(
                                                    imageVector = it,
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.height(16.dp))

                            // Personal UI Section
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(Res.string.app_dev_name),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = stringResource(Res.string.app_dev_role),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                
                                // Subtle icon indicating admin/settings
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .alpha(0.3f),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Clickable Secret Admin Link
                            Text(
                                text = stringResource(Res.string.profile_admin),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier
                                    .alpha(0.2f)
                                    .clickable {
                                        scope.launch { drawerState.close() }
                                        navigator.navigate(ProfileNavKey.Profile)
                                    },
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        ) {
            // We wrap the main content back in LTR so it's not flipped.
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                content()
            }
        }
    }
}
