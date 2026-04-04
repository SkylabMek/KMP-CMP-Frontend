package th.skylabmek.kmp_frontend.features.app_features.profile.presentation.ui.admin.performance

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.koin.compose.viewmodel.koinViewModel
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.domain.model.performances.Performance
import th.skylabmek.kmp_frontend.domain.model.performances.CreatePerformanceRequest
import th.skylabmek.kmp_frontend.features.feature.app.presentation.viewmodel.AppViewModel
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.ui.performance.PerformanceSection
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.components.performance.privatePerformance.PerformanceFullContent
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.components.performance.privatePerformance.PerformanceCreateButton
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.components.performance.privatePerformance.PerformanceCreateInfoDialog
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.viewmodel.PerformanceListViewModel

@Composable
fun PerformanceScreenAdmin(
    appViewModel: AppViewModel,
    profileId: String,
    appId: String = "skylabmek-portfolio", // Default or injected appId
    performanceListViewModel: PerformanceListViewModel = koinViewModel()
) {
    var selectedPerformance by remember { mutableStateOf<Performance?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    val createPerformanceState by performanceListViewModel.createPerformanceState.collectAsState()

    // Refresh when creation is successful
    LaunchedEffect(createPerformanceState) {
        if (createPerformanceState is UiState.Success) {
            performanceListViewModel.resetCreatePerformanceState()
            showCreateDialog = false
        }
    }

    Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            PerformanceSection(
                appViewModel = appViewModel,
                performanceListViewModel = performanceListViewModel,
                onPerformanceClick = { performance ->
                    selectedPerformance = performance
                },
                profileId = profileId,
                appId = appId,
            )
            
            // Add spacing to ensure content doesn't get hidden behind the FAB
            // when it scrolls to the very bottom.
            Spacer(modifier = Modifier.height(80.dp))
        }

        PerformanceCreateButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }

    if (showCreateDialog) {
        PerformanceCreateInfoDialog(
            onDismissRequest = { 
                showCreateDialog = false
                performanceListViewModel.resetCreatePerformanceState()
            },
            onSave = { request: CreatePerformanceRequest ->
                performanceListViewModel.createPerformance(profileId, request)
            }
        )
    }

    if (selectedPerformance != null) {
        Dialog(
            onDismissRequest = { selectedPerformance = null },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.85f),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                PerformanceFullContent(
                    performance = selectedPerformance!!,
                    profileId = profileId,
                    onClose = { 
                        selectedPerformance = null
                        // Refresh the list when returning from detail/editor in case changes were made
                        performanceListViewModel.loadPerformances(profileId)
                    }
                )
            }
        }
    }
}
