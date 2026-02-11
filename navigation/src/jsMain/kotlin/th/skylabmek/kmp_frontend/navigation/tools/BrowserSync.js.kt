package th.skylabmek.kmp_frontend.navigation.tools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.navigation3.runtime.NavBackStack
import kotlinx.browser.window
import kotlinx.coroutines.flow.collectLatest

@Composable
actual fun <T : NavKey> NavBackStack<T>.SyncWithBrowser(providers: List<FeatureNavProvider>) {
    LaunchedEffect(this) {
        window.onpopstate = { _ ->
            if (this@SyncWithBrowser.size > 1) {
                this@SyncWithBrowser.removeAt(this@SyncWithBrowser.size - 1)
            }
        }

        var lastSize = this@SyncWithBrowser.size

        snapshotFlow { this@SyncWithBrowser.toList() }.collectLatest { currentStack ->
            val currentKey = currentStack.lastOrNull() ?: return@collectLatest
            val path = providers.firstNotNullOfOrNull { it.mapNavKeyToUri(currentKey) } ?: "/"
            
            if (currentStack.size > lastSize) {
                window.history.pushState(null, "", path)
            } else if (currentStack.size < lastSize) {
                // Back navigation handled by onpopstate or manual pop
            } else {
                window.history.replaceState(null, "", path)
            }
            lastSize = currentStack.size
        }
    }
}
