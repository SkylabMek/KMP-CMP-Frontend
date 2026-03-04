package th.skylabmek.kmp_frontend.navigation.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import th.skylabmek.kmp_frontend.navigation.tools.FeatureNavProvider
import th.skylabmek.kmp_frontend.navigation.tools.NavKey

/**
 * A generic Navigation Host that uses a list of providers to resolve keys via DSL.
 */
@Composable
fun AppNavHost(
    backStack: NavBackStack<NavKey>,
    onBack: (() -> Unit)?,
    providers: List<FeatureNavProvider>,
    modifier: Modifier = Modifier
) {
    // Stability is key: remember the graph to avoid unnecessary re-calculations
    // and potential rendering glitches in Previews.
    val entryProvider = remember(providers) {
        entryProvider {
            providers.forEach { provider ->
                with(provider) {
                    navigationBuilder()
                }
            }
        }
    }

    // Removed fillMaxSize() to avoid "infinity maximum height constraints" 
    // when nested inside a scrollable Column in AppLayout.
    NavDisplay(
        backStack = backStack,
        onBack = onBack ?: {},
        modifier = modifier.fillMaxWidth(),
        entryProvider = entryProvider
    )
}
