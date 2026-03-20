package th.skylabmek.kmp_frontend.features.app_features.profile.presentation.ui.admin.performance

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import th.skylabmek.kmp_frontend.domain.model.performances.Performance
import th.skylabmek.kmp_frontend.features.feature.app.presentation.viewmodel.AppViewModel
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.ui.performance.PerformanceSection
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.components.performance.privatePerformance.PerformanceFullContent

@Composable
fun PerformanceScreenAdmin(
    appViewModel: AppViewModel,
    profileId: String,
    appId: String = "skylabmek-portfolio" // Default or injected appId
) {
    var selectedPerformance by remember { mutableStateOf<Performance?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        PerformanceSection(
            appViewModel = appViewModel,
            onPerformanceClick = { performance ->
                selectedPerformance = performance
            },
            profileId = profileId,
            appId = appId,
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
                    onClose = { selectedPerformance = null }
                )
            }
        }
    }
}
