package th.skylabmek.kmp_frontend

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import kotlinx.browser.window
import th.skylabmek.kmp_frontend.di.initKoin
import th.skylabmek.kmp_frontend.presentation.ui.App

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin()
    val body = document.body ?: return
    
    // Capture the initial path from the browser to handle initial routing
//    val initialPath = window.location.pathname + window.location.search
    
    ComposeViewport(body) {
        App(
//            deepLinkUri = initialPath
        )
    }
}
