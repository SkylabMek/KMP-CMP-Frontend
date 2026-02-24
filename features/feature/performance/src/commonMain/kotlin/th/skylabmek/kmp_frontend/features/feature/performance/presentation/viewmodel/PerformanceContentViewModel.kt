package th.skylabmek.kmp_frontend.features.feature.performance.presentation.viewmodel

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

    private val _updateState = MutableStateFlow<UiState<PerformanceUpdateResult>?>(null)
    val updateState: StateFlow<UiState<PerformanceUpdateResult>?> = _updateState.asStateFlow()

    private val _deleteState = MutableStateFlow<UiState<PerformanceDeleteResult>?>(null)
    val deleteState: StateFlow<UiState<PerformanceDeleteResult>?> = _deleteState.asStateFlow()

    /**
     * Loads performance content using its direct URL.
     */
    fun loadPerformanceContent(url: String) {
        viewModelScope.launch {
            _performanceContentState.value = UiState.Loading
            when (val result = performanceRepository.getPerformanceContentFromUrl(url)) {
                is NetworkResult.Success -> {
                    _performanceContentState.value = UiState.Success(
                        PerformanceContentResult(contentMarkdown = result.data)
                    )
                    println("NetworkResult.Success with data: ${result.data}")
                }
                is NetworkResult.Failure -> {
                    _performanceContentState.value = UiState.Error(
                        UiError.StringRes(result.error.asStringResource())
                    )
                    println("NetworkResult.Failure with error: ${result.error}")
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
                }
                is NetworkResult.Failure -> {
                    _updateState.value = UiState.Error(
                        UiError.StringRes(result.error.asStringResource())
                    )
                }
            }
        }
    }

    fun deletePerformance(profileId: String, performanceId: String) {
        viewModelScope.launch {
            _deleteState.value = UiState.Loading
            when (val result = performanceRepository.deletePerformance(profileId, performanceId)) {
                is NetworkResult.Success -> {
                    _deleteState.value = UiState.Success(result.data)
                }
                is NetworkResult.Failure -> {
                    _deleteState.value = UiState.Error(
                        UiError.StringRes(result.error.asStringResource())
                    )
                }
            }
        }
    }

    fun resetUpdateState() {
        _updateState.value = null
    }

    fun resetDeleteState() {
        _deleteState.value = null
    }
}
