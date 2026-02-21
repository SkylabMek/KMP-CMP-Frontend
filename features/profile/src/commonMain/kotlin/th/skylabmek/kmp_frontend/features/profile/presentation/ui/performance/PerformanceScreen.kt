package th.skylabmek.kmp_frontend.features.profile.presentation.ui.performance

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import th.skylabmek.kmp_frontend.features.app.presentation.viewmodel.AppViewModel
import th.skylabmek.kmp_frontend.features.performance.presentation.ui.performance.PerformanceSection
import th.skylabmek.kmp_frontend.features.performance.presentation.components.performance.PerformanceFullContent

@Composable
fun PerformanceScreen(
    appViewModel: AppViewModel,
    profileId: String,
    appId: String = "skylabmek-portfolio" // Default or injected appId
) {
    var selectedPerformanceId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        PerformanceSection(
            appViewModel = appViewModel,
            onPerformanceClick = { id ->
                selectedPerformanceId = id
            },
            profileId = profileId,
            appId = appId,
        )
    }

    if (selectedPerformanceId != null) {
        Dialog(
            onDismissRequest = { selectedPerformanceId = null },
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
                    profileId = profileId,
                    performanceId = selectedPerformanceId!!,
                    onClose = { selectedPerformanceId = null },
//                    onEdit = { /* TODO: Implement navigation to edit screen */ }
                )
            }
        }
    }
}
