package th.skylabmek.kmp_frontend.navigation.tools

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavBackStack

@Composable
actual fun <T : NavKey> NavBackStack<T>.SyncWithBrowser(providers: List<FeatureNavProvider>) {
    // No-op for iOS
}
