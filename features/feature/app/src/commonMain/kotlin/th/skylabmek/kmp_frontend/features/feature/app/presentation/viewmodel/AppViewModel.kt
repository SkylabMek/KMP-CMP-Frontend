package th.skylabmek.kmp_frontend.features.feature.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import th.skylabmek.kmp_frontend.core.common.UiError
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.core.network.result.NetworkResult
import th.skylabmek.kmp_frontend.domain.model.feature.AppFeatureStatus
import th.skylabmek.kmp_frontend.domain.model.feature.FeatureCode
import th.skylabmek.kmp_frontend.domain.model.feature.FeatureStatusCode
import th.skylabmek.kmp_frontend.domain.repository.feature.FeatureRepository

class AppViewModel(
    private val featureRepository: FeatureRepository
) : ViewModel() {

    private var hasLoadedOnce = false

    private val _uiState = MutableStateFlow<UiState<List<AppFeatureStatus>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<AppFeatureStatus>>> = _uiState.asStateFlow()

    fun getFeatureStatusByCode(appId: String, featureCode: FeatureCode): StateFlow<UiState<FeatureStatusCode>> {
        getOrLoadFeatureStatus(appId)
        
        return uiState.map { state ->
            when (state) {
                is UiState.Loading -> UiState.Loading
                is UiState.Error -> UiState.Error(state.uiError)
                is UiState.Success -> {
                    val feature = state.data.find { it.featureCode == featureCode }
                    if (feature != null) {
                        UiState.Success(feature.statusCode)
                    } else {
                        UiState.Error(UiError.DynamicString("Feature $featureCode not found"))
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )
    }

    fun getOrLoadFeatureStatus(appId: String): StateFlow<UiState<List<AppFeatureStatus>>> {
        if (!hasLoadedOnce) {
            hasLoadedOnce = true
            loadFeatureStatus(appId)
        }
        return uiState
    }

    fun loadFeatureStatus(appId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = featureRepository.getFeatureStatus(appId)) {
                is NetworkResult.Success -> {
                    _uiState.value = UiState.Success(result.data.features)
                }
                is NetworkResult.Failure -> {
                    _uiState.value = UiState.Error(
                        UiError.StringRes(result.error.asStringResource())
                    )
                }
            }
        }
    }
}
