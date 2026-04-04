package th.skylabmek.kmp_frontend.ui.config

import androidx.compose.runtime.compositionLocalOf
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.domain.model.app.AppConfig

val LocalAppConfig = compositionLocalOf<UiState<AppConfig>> {
    UiState.Loading // default value before config loads
}
