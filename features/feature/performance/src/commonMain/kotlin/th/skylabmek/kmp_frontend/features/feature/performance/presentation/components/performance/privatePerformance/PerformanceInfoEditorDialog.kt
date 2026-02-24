package th.skylabmek.kmp_frontend.features.feature.performance.presentation.components.performance.privatePerformance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import th.skylabmek.kmp_frontend.domain.model.performances.Performance
import th.skylabmek.kmp_frontend.domain.model.performances.UpdatePerformanceRequest
import th.skylabmek.kmp_frontend.features.feature.performance.model.PerformanceCategoryUi
import th.skylabmek.kmp_frontend.features.feature.performance.model.VisibilityUi
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.*
import th.skylabmek.kmp_frontend.ui.components.dialog.ConfirmDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceInfoEditorDialog(
    performance: Performance,
    onDismissRequest: () -> Unit,
    onSave: (UpdatePerformanceRequest) -> Unit,
    onDelete: () -> Unit
) {
    var title by remember { mutableStateOf(performance.title) }
    var summary by remember { mutableStateOf(performance.summary ?: "") }
    var location by remember { mutableStateOf(performance.location ?: "") }
    var startDate by remember { mutableStateOf(performance.startDate ?: "") }
    var endDate by remember { mutableStateOf(performance.endDate ?: "") }
    var isClosed by remember { mutableStateOf(performance.close) }
    
    var selectedCategory by remember { 
        mutableStateOf(PerformanceCategoryUi.fromId(performance.categoryId) ?: PerformanceCategoryUi.PROJECT)
    }
    var selectedVisibility by remember { 
        mutableStateOf(VisibilityUi.fromId(performance.visibilityId) ?: VisibilityUi.DRAFT)
    }

    var categoryExpanded by remember { mutableStateOf(false) }
    var visibilityExpanded by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        ConfirmDialog(
            title = stringResource(Res.string.dialog_performance_delete_title),
            message = stringResource(Res.string.dialog_performance_delete_message),
            confirmText = stringResource(Res.string.dialog_performance_delete_confirm),
            isDangerous = true,
            onConfirm = {
                onDelete()
                onDismissRequest()
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(Res.string.performance_editor_title))
                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(Res.string.performance_editor_delete_desc),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(Res.string.performance_editor_label_title)) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text(stringResource(Res.string.performance_editor_label_summary)) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                // Category Selector
                Box {
                    OutlinedTextField(
                        value = stringResource(selectedCategory.displayNameRes),
                        onValueChange = {},
                        label = { Text(stringResource(Res.string.performance_editor_label_category)) },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, null, Modifier.clickable { categoryExpanded = true })
                        }
                    )
                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        PerformanceCategoryUi.entries.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(stringResource(category.displayNameRes)) },
                                onClick = {
                                    selectedCategory = category
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                // Visibility Selector
                Box {
                    OutlinedTextField(
                        value = stringResource(selectedVisibility.displayNameRes),
                        onValueChange = {},
                        label = { Text(stringResource(Res.string.performance_editor_label_visibility)) },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, null, Modifier.clickable { visibilityExpanded = true })
                        }
                    )
                    DropdownMenu(
                        expanded = visibilityExpanded,
                        onDismissRequest = { visibilityExpanded = false }
                    ) {
                        VisibilityUi.entries.forEach { visibility ->
                            DropdownMenuItem(
                                text = { Text(stringResource(visibility.displayNameRes)) },
                                onClick = {
                                    selectedVisibility = visibility
                                    visibilityExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(stringResource(Res.string.performance_editor_label_location)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text(stringResource(Res.string.performance_editor_label_start_date)) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(stringResource(Res.string.performance_editor_date_placeholder)) }
                    )
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text(stringResource(Res.string.performance_editor_label_end_date)) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(stringResource(Res.string.performance_editor_date_placeholder)) }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isClosed, onCheckedChange = { isClosed = it })
                    Text(stringResource(Res.string.performance_editor_label_closed))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        UpdatePerformanceRequest(
                            categoryId = selectedCategory.id,
                            visibilityId = selectedVisibility.id,
                            title = title,
                            summary = summary.ifBlank { null },
                            location = location.ifBlank { null },
                            startDate = startDate.ifBlank { null },
                            endDate = endDate.ifBlank { null },
                            close = isClosed
                        )
                    )
                }
            ) {
                Text(stringResource(Res.string.dialog_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(Res.string.dialog_cancel))
            }
        }
    )
}
