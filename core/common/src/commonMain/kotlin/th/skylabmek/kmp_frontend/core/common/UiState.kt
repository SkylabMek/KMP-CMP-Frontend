package th.skylabmek.kmp_frontend.core.common

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val uiError: UiError) : UiState<Nothing>
}

sealed class UiError {
    data class StringRes(val res: StringResource) : UiError()
    data class DynamicString(val message: String) : UiError()
}

/**
 * Resolves the [UiError] to a [String] within a Composable context.
 */
@Composable
fun UiError.asString(): String {
    return when (this) {
        is UiError.StringRes -> stringResource(res)
        is UiError.DynamicString -> message
    }
}
