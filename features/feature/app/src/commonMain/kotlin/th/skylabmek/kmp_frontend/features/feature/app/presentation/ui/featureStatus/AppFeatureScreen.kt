package th.skylabmek.kmp_frontend.features.feature.app.presentation.ui.featureStatus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.core.common.errorMessage
import th.skylabmek.kmp_frontend.domain.model.feature.AppFeatureStatus
import th.skylabmek.kmp_frontend.features.feature.app.presentation.viewmodel.AppViewModel
import th.skylabmek.kmp_frontend.features.feature.app.presentation.viewmodel.AppViewModel

@Composable
fun AppFeatureScreen(
    websiteId: String,
    viewModel: AppViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(websiteId) {
        viewModel.loadFeatureStatus(websiteId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Website Feature Status",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (val state = uiState) {
            is UiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
            is UiState.Success -> {
                FeatureList(state.data)
            }
            is UiState.Error -> {
                Text(
                    text = state.uiError.errorMessage()(),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(onClick = { viewModel.loadFeatureStatus(websiteId) }) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
fun FeatureList(features: List<AppFeatureStatus>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(features) { feature ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = feature.featureName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Status: ${feature.statusName}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Updated: ${feature.updatedAt}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (feature.isClosed) {
                        Text(
                            text = "CLOSED",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    feature.note?.let {
                        Text(
                            text = "Note: $it",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
