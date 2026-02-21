package th.skylabmek.kmp_frontend.features.feature.performance.presentation.components.performance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import th.skylabmek.kmp_frontend.domain.model.performances.Performance
import th.skylabmek.kmp_frontend.domain.model.performances.UpdatePerformanceRequest
import th.skylabmek.kmp_frontend.features.feature.performance.model.PerformanceCategoryUi
import th.skylabmek.kmp_frontend.features.feature.performance.model.VisibilityUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceInfoEditorDialog(
    performance: Performance,
    onDismissRequest: () -> Unit,
    onSave: (UpdatePerformanceRequest) -> Unit
) {
    var title by remember { mutableStateOf(performance.title) }
    var summary by remember { mutableStateOf(performance.summary ?: "") }
    var location by remember { mutableStateOf(performance.location ?: "") }
    var startDate by remember { mutableStateOf(performance.startDate ?: "") }
    var endDate by remember { mutableStateOf(performance.endDate ?: "") }
    var isClosed by remember { mutableStateOf(performance.close) }
    
    var selectedCategory by remember { 
        mutableStateOf(PerformanceCategoryUi.Companion.fromId(performance.categoryId) ?: PerformanceCategoryUi.PROJECT)
    }
    var selectedVisibility by remember { 
        mutableStateOf(VisibilityUi.Companion.fromId(performance.visibilityId) ?: VisibilityUi.DRAFT)
    }

    var categoryExpanded by remember { mutableStateOf(false) }
    var visibilityExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Edit Performance Information") },
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
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Summary") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                // Category Selector
                Box {
                    OutlinedTextField(
                        value = selectedCategory.displayName,
                        onValueChange = {},
                        label = { Text("Category") },
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
                        PerformanceCategoryUi.values().forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.displayName) },
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
                        value = selectedVisibility.displayName,
                        onValueChange = {},
                        label = { Text("Visibility") },
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
                        VisibilityUi.values().forEach { visibility ->
                            DropdownMenuItem(
                                text = { Text(visibility.displayName) },
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
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = { startDate = it },
                        label = { Text("Start Date") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("YYYY-MM-DD") }
                    )
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = { endDate = it },
                        label = { Text("End Date") },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("YYYY-MM-DD") }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isClosed, onCheckedChange = { isClosed = it })
                    Text("Closed")
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
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}
