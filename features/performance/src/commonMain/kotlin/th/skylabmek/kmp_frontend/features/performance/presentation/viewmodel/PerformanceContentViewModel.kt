package th.skylabmek.kmp_frontend.features.performance.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import th.skylabmek.kmp_frontend.core.common.UiError
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.core.network.result.NetworkResult
import th.skylabmek.kmp_frontend.domain.model.performances.*
import th.skylabmek.kmp_frontend.domain.repository.performances.PerformanceRepository

class PerformanceContentViewModel(
    private val performanceRepository: PerformanceRepository
) : ViewModel() {

    private val _performanceContentState = MutableStateFlow<UiState<PerformanceContentResult>>(UiState.Loading)
    val performanceContentState: StateFlow<UiState<PerformanceContentResult>> = _performanceContentState.asStateFlow()

    private val _performanceState = MutableStateFlow<UiState<Performance>>(UiState.Loading)
    val performanceState: StateFlow<UiState<Performance>> = _performanceState.asStateFlow()

    private val _updateState = MutableStateFlow<UiState<PerformanceUpdateResult>?>(null)
    val updateState: StateFlow<UiState<PerformanceUpdateResult>?> = _updateState.asStateFlow()

    fun loadPerformance(profileId: String, performanceId: String) {
        viewModelScope.launch {
            _performanceState.value = UiState.Loading
            // Since there's no direct getPerformance by ID, we fetch the list and find it.
            // In a real app, you'd likely have a getPerformance(id) endpoint.
            when (val result = performanceRepository.getPerformances(profileId)) {
                is NetworkResult.Success -> {
                    val performance = result.data.performances.find { it.id == performanceId }
                    if (performance != null) {
                        _performanceState.value = UiState.Success(performance)
                    } else {
                        _performanceState.value = UiState.Error(UiError.DynamicString("Performance not found"))
                    }
                }
                is NetworkResult.Failure -> {
                    _performanceState.value = UiState.Error(
                        UiError.StringRes(result.error.asStringResource())
                    )
                }
            }
        }
    }

    fun loadPerformanceContent(profileId: String, performanceId: String) {
        viewModelScope.launch {
            _performanceContentState.value = UiState.Loading
            when (val result = performanceRepository.getPerformanceContent(profileId, performanceId)) {
                is NetworkResult.Success -> {
                    _performanceContentState.value = UiState.Success(result.data)
                }
                is NetworkResult.Failure -> {
                    _performanceContentState.value = UiState.Error(
                        UiError.StringRes(result.error.asStringResource())
                    )
                }
            }
        }
    }

    fun updatePerformance(
        profileId: String,
        performanceId: String,
        request: UpdatePerformanceRequest
    ) {
        viewModelScope.launch {
            _updateState.value = UiState.Loading
            when (val result = performanceRepository.updatePerformance(profileId, performanceId, request)) {
                is NetworkResult.Success -> {
                    _updateState.value = UiState.Success(result.data)
                    // Refresh performance data
                    loadPerformance(profileId, performanceId)
                }
                is NetworkResult.Failure -> {
                    _updateState.value = UiState.Error(
                        UiError.StringRes(result.error.asStringResource())
                    )
                }
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = null
    }

    fun loadPerformanceContentFromUrl(url: String) {
        viewModelScope.launch {
            _performanceContentState.value = UiState.Loading
            when (val result = performanceRepository.getPerformanceContentFromUrl(url)) {
                is NetworkResult.Success -> {
                    _performanceContentState.value = UiState.Success(
                        PerformanceContentResult(contentMarkdown = result.data)
                    )
                }
                is NetworkResult.Failure -> {
                    _performanceContentState.value = UiState.Error(
                        UiError.StringRes(result.error.asStringResource())
                    )
                }
            }
        }
    }
}
