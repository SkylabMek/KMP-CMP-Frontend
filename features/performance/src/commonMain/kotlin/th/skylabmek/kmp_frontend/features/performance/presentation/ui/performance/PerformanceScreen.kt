package th.skylabmek.kmp_frontend.features.performance.presentation.ui.performance

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import th.skylabmek.kmp_frontend.features.app.presentation.viewmodel.AppViewModel
import th.skylabmek.kmp_frontend.features.performance.presentation.components.performance.PerformanceFullContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceScreen(
    appViewModel: AppViewModel,
    profileId: String,
    appId: String = "skylabmek-portfolio" // Default or injected appId
) {
    var selectedPerformanceId by remember { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Performances") }
            )
        }
    ) { paddingValues ->
        PerformanceSection(
            appViewModel = appViewModel,
            onPerformanceClick = { id ->
                selectedPerformanceId = id
                showBottomSheet = true
            },
            profileId = profileId,
            appId = appId,
            modifier = Modifier.padding(paddingValues)
        )
    }

    if (showBottomSheet && selectedPerformanceId != null) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                selectedPerformanceId = null
            },
            sheetState = sheetState,
            modifier = Modifier.fillMaxHeight(0.9f)
        ) {
            PerformanceFullContent(
                profileId = profileId,
                performanceId = selectedPerformanceId!!
            )
        }
    }
}
