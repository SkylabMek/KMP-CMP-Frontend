package th.skylabmek.kmp_frontend.navigation.tools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation3.runtime.NavBackStack
import kotlinx.browser.window

@OptIn(ExperimentalWasmJsInterop::class)
@Composable
actual fun <T : NavKey> NavBackStack<T>.SyncWithBrowser(providers: List<FeatureNavProvider>) {
    LaunchedEffect(this) {
        window.onpopstate = { _ ->
            if (this@SyncWithBrowser.size > 1) {
                this@SyncWithBrowser.removeAt(this@SyncWithBrowser.size - 1)
            }
        }
    }

    CollectStackChanges(providers) { path, isPush, isReplace ->
        if (isPush) {
            window.history.pushState(null, "", "#$path")  // ← hash routing
        } else if (isReplace) {
            window.history.replaceState(null, "", "#$path")
        }
    }
}
