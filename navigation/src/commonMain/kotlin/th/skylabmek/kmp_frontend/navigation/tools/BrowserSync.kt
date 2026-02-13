package th.skylabmek.kmp_frontend.navigation.tools

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.navigation3.runtime.NavBackStack
import kotlinx.coroutines.flow.collectLatest

/**
 * Extension to sync the [NavBackStack] with the browser's history and URL bar.
 * This is an [expect] function with platform-specific implementations.
 */
@Composable
expect fun <T : NavKey> NavBackStack<T>.SyncWithBrowser(providers: List<FeatureNavProvider>)

/**
 * Internal helper to collect backstack changes and map them to URI paths.
 * Used by platform implementations to keep browser history in sync.
 */
@Composable
fun <T : NavKey> NavBackStack<T>.CollectStackChanges(
    providers: List<FeatureNavProvider>,
    onStackChanged: (path: String, isPush: Boolean, isReplace: Boolean) -> Unit
) {
    LaunchedEffect(this) {
        var lastSize = this@CollectStackChanges.size

        snapshotFlow { this@CollectStackChanges.toList() }.collectLatest { currentStack ->
            val currentKey = currentStack.lastOrNull() ?: return@collectLatest
            val path = providers.firstNotNullOfOrNull { it.mapNavKeyToUri(currentKey) } ?: "/"
            
            val isPush = currentStack.size > lastSize
            val isReplace = currentStack.size == lastSize
            
            onStackChanged(path, isPush, isReplace)
            lastSize = currentStack.size
        }
    }
}
