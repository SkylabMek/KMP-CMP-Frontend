package th.skylabmek.kmp_frontend.ui.components.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import th.skylabmek.kmp_frontend.core.common.UiError
import th.skylabmek.kmp_frontend.core.common.asString
import th.skylabmek.kmp_frontend.ui.dimens.Dimens

@Composable
fun DefaultLoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.paddingMedium),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun DefaultErrorContent(
    error: UiError,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    showRetry: Boolean = true
) {
    // Directly resolve the message using the composable extension
    val message = error.asString()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.paddingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        if (showRetry) {
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}
