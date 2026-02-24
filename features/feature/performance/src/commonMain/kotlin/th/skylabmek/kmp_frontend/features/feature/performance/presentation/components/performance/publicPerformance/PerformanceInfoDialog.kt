package th.skylabmek.kmp_frontend.features.feature.performance.presentation.components.performance.publicPerformance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import th.skylabmek.kmp_frontend.domain.model.performances.Performance
import th.skylabmek.kmp_frontend.features.feature.performance.model.PerformanceCategoryUi
import th.skylabmek.kmp_frontend.features.feature.performance.model.VisibilityUi
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.*

@Composable
fun PerformanceInfoDialog(
    performance: Performance,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(Res.string.performance_detail_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoRow(label = stringResource(Res.string.performance_editor_label_title), value = performance.title)
                performance.summary?.takeIf { it.isNotBlank() }?.let { InfoRow(label = stringResource(Res.string.performance_editor_label_summary), value = it) }
                InfoRow(
                    label = stringResource(Res.string.performance_editor_label_category),
                    value = PerformanceCategoryUi.fromId(performance.categoryId)?.let { stringResource(it.displayNameRes) } ?: "N/A"
                )
                InfoRow(
                    label = stringResource(Res.string.performance_editor_label_visibility),
                    value = VisibilityUi.fromId(performance.visibilityId)?.let { stringResource(it.displayNameRes) } ?: "N/A"
                )
                performance.location?.takeIf { it.isNotBlank() }?.let { InfoRow(label = stringResource(Res.string.performance_editor_label_location), value = it) }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    performance.startDate?.takeIf { it.isNotBlank() }?.let { InfoRow(label = stringResource(Res.string.performance_editor_label_start_date), value = it, modifier = Modifier.weight(1f)) }
                    performance.endDate?.takeIf { it.isNotBlank() }?.let { InfoRow(label = stringResource(Res.string.performance_editor_label_end_date), value = it, modifier = Modifier.weight(1f)) }
                }
                InfoRow(
                    label = stringResource(Res.string.performance_editor_label_closed),
                    value = if (performance.close) "Yes" else "No" // Could also use resources for Yes/No
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(Res.string.dialog_close))
            }
        }
    )
}

@Composable
private fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
