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
            // If the browser goes back, we pop our stack if we can
            if (this@SyncWithBrowser.size > 1) {
                this@SyncWithBrowser.removeAt(this@SyncWithBrowser.size - 1)
            }
        }

        // Keep track of the last seen backstack size to decide whether to push or replace
        var lastSize = this@SyncWithBrowser.size

        snapshotFlow { this@SyncWithBrowser.toList() }.collectLatest { currentStack ->
            val currentKey = currentStack.lastOrNull() ?: return@collectLatest
            
            // Map the key to a URI path
            val path = providers.firstNotNullOfOrNull { it.mapNavKeyToUri(currentKey) } ?: "/"
            
            if (currentStack.size > lastSize) {
                // Navigated forward
                window.history.pushState(null, "", path)
            } else if (currentStack.size < lastSize) {
                // Navigated backward (likely via our own Navigator.back())
                // We don't necessarily want to call history.back() here because 
                // it might have been triggered by onpopstate already.
                // But if it was triggered by app logic, we might need to sync.
                // However, standard browser behavior is that onpopstate is the source of truth for back.
            } else {
                // Replaced or initial
                window.history.replaceState(null, "", path)
            }
            lastSize = currentStack.size
        }
    }
}
