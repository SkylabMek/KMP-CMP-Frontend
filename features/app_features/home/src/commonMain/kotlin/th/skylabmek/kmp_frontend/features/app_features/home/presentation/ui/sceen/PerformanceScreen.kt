package th.skylabmek.kmp_frontend.features.app_features.home.presentation.ui.sceen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.domain.model.performances.Performance
import th.skylabmek.kmp_frontend.features.app_features.profile.presentation.viewmodel.ProfileViewModel
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.components.performance.publicPerformance.PerformanceFullContent
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.components.performance.privatePerformance.PerformanceShowGrid
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.*
import th.skylabmek.kmp_frontend.ui.components.dialog.FullScreenDialog
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultErrorContent
import th.skylabmek.kmp_frontend.ui.components.layout.DefaultLoadingContent
import th.skylabmek.kmp_frontend.ui.dimens.Dimens

/**
 * Data model for category display information.
 */
private data class CategoryDisplayInfo(
    val titleRes: StringResource,
    val descriptionRes: StringResource? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceScreen(
    profileId: String,
    onBack: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val performancesState by viewModel.getOrLoadPerformances(profileId).collectAsState()
    var selectedPerformance by remember { mutableStateOf<Performance?>(null) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.screenPadding)
    ) {
        when (val state = performancesState) {
            is UiState.Loading -> {
                DefaultLoadingContent(modifier = Modifier.fillMaxWidth().height(300.dp))
            }

            is UiState.Error -> {
                DefaultErrorContent(
                    error = state.uiError,
                    onRetry = { viewModel.loadPerformances(profileId) }
                )
            }

            is UiState.Success -> {
                val performances = state.data.performances.sortedByDescending { it.updatedAt ?: it.createdAt }
                
                if (performances.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(Dimens.spaceExtraLarge), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No performances available.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    val groupedPerformances = performances.groupBy { it.categoryId }
                        .filter { it.value.isNotEmpty() }

                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val isDesktop = maxWidth > 800.dp
                        val sectionSpacing = Dimens.spaceLarge

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(sectionSpacing)
                        ) {
                            if (isDesktop) {
                                val categories = groupedPerformances.keys.toList()
                                categories.chunked(2).forEach { rowCategories ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(sectionSpacing)
                                    ) {
                                        rowCategories.forEach { categoryId ->
                                            Column(modifier = Modifier.weight(1f)) {
                                                CategoryHeader(categoryId)
                                                PerformanceShowGrid(
                                                    performances = groupedPerformances[categoryId] ?: emptyList(),
                                                    onPerformanceClick = { selectedPerformance = it }
                                                )
                                            }
                                        }
                                        if (rowCategories.size < 2) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            } else {
                                groupedPerformances.forEach { (categoryId, performanceList) ->
                                    CategoryHeader(categoryId)
                                    PerformanceShowGrid(
                                        performances = performanceList,
                                        onPerformanceClick = { selectedPerformance = it }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    selectedPerformance?.let { performance ->
        FullScreenDialog(
            onDismissRequest = { selectedPerformance = null }
        ) {
            PerformanceFullContent(
                performance = performance,
                onClose = { selectedPerformance = null }
            )
        }
    }
}

@Composable
private fun CategoryHeader(categoryId: String) {
    // Map categoryId to display info using string resources
    val displayInfo = when (categoryId) {
        "perf_cat_project" -> CategoryDisplayInfo(Res.string.perf_cat_project_name, Res.string.perf_cat_project_desc)
        "perf_cat_achievement" -> CategoryDisplayInfo(Res.string.perf_cat_achievement_name, Res.string.perf_cat_achievement_desc)
        "perf_cat_demo" -> CategoryDisplayInfo(Res.string.perf_cat_demo_name, Res.string.perf_cat_demo_desc)
        "perf_cat_performance" -> CategoryDisplayInfo(Res.string.perf_cat_performance_name, Res.string.perf_cat_performance_desc)
        "perf_cat_publication" -> CategoryDisplayInfo(Res.string.perf_cat_publication_name, Res.string.perf_cat_publication_desc)
        "perf_cat_work" -> CategoryDisplayInfo(Res.string.perf_cat_work_name, Res.string.perf_cat_work_desc)
        else -> null
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = displayInfo?.let { stringResource(it.titleRes) } ?: categoryId,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = Dimens.itemSpacingSmall)
        )
        
        displayInfo?.descriptionRes?.let {
            Text(
                text = stringResource(it),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = Dimens.itemSpacingSmall)
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(bottom = Dimens.itemSpacingMedium),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}
