package th.skylabmek.kmp_frontend.features.feature.performance.presentation.components.performance.privatePerformance

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import th.skylabmek.kmp_frontend.domain.model.performances.Performance
import th.skylabmek.kmp_frontend.ui.dimens.Dimens

@Composable
fun PerformanceShowGrid(
    performances: List<Performance>,
    onPerformanceClick: (Performance) -> Unit,
    modifier: Modifier = Modifier,
    preferredColumnCount: Int? = null
) {
    val spacing = Dimens.itemSpacingMedium

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val containerWidth = maxWidth
        
        // Calculate columns based on the actual width available to this component.
        val columnCount = preferredColumnCount ?: when {
            containerWidth < 400.dp -> 2
            containerWidth < 750.dp -> 3
            containerWidth < 1100.dp -> 4
            else -> 5
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
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
                    // Fill empty slots if any to maintain card size and alignment
                    if (rowItems.size < columnCount) {
                        repeat(columnCount - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
