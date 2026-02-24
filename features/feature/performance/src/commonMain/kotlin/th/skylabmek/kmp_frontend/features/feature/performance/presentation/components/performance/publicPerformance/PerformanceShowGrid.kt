package th.skylabmek.kmp_frontend.features.feature.performance.presentation.components.performance.publicPerformance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import th.skylabmek.kmp_frontend.domain.model.performances.Performance
import th.skylabmek.kmp_frontend.ui.config.UI
import th.skylabmek.kmp_frontend.ui.dimens.Dimens

@Composable
fun PerformanceShowGrid(
    performances: List<Performance>,
    onPerformanceClick: (Performance) -> Unit,
    modifier: Modifier = Modifier
) {
    val columnCount = when {
        UI.isDesktop -> 3
        UI.isTablet -> 2
        else -> 1
    }

    val spacing = Dimens.itemSpacingMedium

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        performances.chunked(columnCount).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                rowItems.forEach { performance ->
                    PerformanceShowItem(
                        performance = performance,
                        onClick = { onPerformanceClick(performance) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill empty slots if any
                if (rowItems.size < columnCount) {
                    repeat(columnCount - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
