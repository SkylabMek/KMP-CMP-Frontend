package th.skylabmek.kmp_frontend.features.feature.performance.presentation.components.performance.publicPerformance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import th.skylabmek.kmp_frontend.domain.model.performances.Performance
import th.skylabmek.kmp_frontend.features.feature.performance.model.PerformanceCategoryUi
import th.skylabmek.kmp_frontend.features.feature.performance.model.VisibilityUi
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.*
import th.skylabmek.kmp_frontend.ui.dimens.Dimens

@Composable
fun PerformanceInfoDialog(
    performance: Performance,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(Res.string.performance_detail_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
            ) {
                // Title Section
                InfoRow(
                    label = stringResource(Res.string.performance_editor_label_title),
                    value = performance.title,
                    icon = Icons.Default.Subtitles
                )

                // Summary Section
                performance.summary?.takeIf { it.isNotBlank() }?.let {
                    InfoRow(
                        label = stringResource(Res.string.performance_editor_label_summary),
                        value = it,
                        icon = Icons.AutoMirrored.Filled.Notes
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.spaceExtraSmall), thickness = 0.5.dp)

                // Metadata Section
                Row(modifier = Modifier.fillMaxWidth()) {
                    InfoRow(
                        label = stringResource(Res.string.performance_editor_label_category),
                        value = PerformanceCategoryUi.fromId(performance.categoryId)?.let { stringResource(it.displayNameRes) } ?: "N/A",
                        icon = Icons.Default.Category,
                        modifier = Modifier.weight(1f)
                    )
                    InfoRow(
                        label = stringResource(Res.string.performance_editor_label_visibility),
                        value = VisibilityUi.fromId(performance.visibilityId)?.let { stringResource(it.displayNameRes) } ?: "N/A",
                        icon = Icons.Default.Visibility,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Location Section
                performance.location?.takeIf { it.isNotBlank() }?.let {
                    InfoRow(
                        label = stringResource(Res.string.performance_editor_label_location),
                        value = it,
                        icon = Icons.Default.Place
                    )
                }

                // Dates Section
                Row(modifier = Modifier.fillMaxWidth()) {
                    performance.startDate?.takeIf { it.isNotBlank() }?.let {
                        InfoRow(
                            label = stringResource(Res.string.performance_editor_label_start_date),
                            value = it,
                            icon = Icons.Default.DateRange,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    performance.endDate?.takeIf { it.isNotBlank() }?.let {
                        InfoRow(
                            label = stringResource(Res.string.performance_editor_label_end_date),
                            value = it,
                            icon = Icons.Default.EventAvailable,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = Dimens.spaceExtraSmall), thickness = 0.5.dp)

                // Status Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
                ) {
                    Icon(
                        imageVector = if (performance.close) Icons.Default.CheckCircle else Icons.Default.RunningWithErrors,
                        contentDescription = null,
                        tint = if (performance.close) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = stringResource(Res.string.performance_editor_label_closed),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (performance.close) "Completed" else "In Progress",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (performance.close) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismissRequest,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = stringResource(Res.string.dialog_close),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    )
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
        verticalAlignment = Alignment.Top
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp).padding(top = 2.dp)
            )
        }
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
            )
        }
    }
}
