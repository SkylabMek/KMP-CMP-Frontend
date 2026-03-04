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
import th.skylabmek.kmp_frontend.domain.model.performances.CreatePerformanceRequest
import th.skylabmek.kmp_frontend.domain.model.performances.Performance
import th.skylabmek.kmp_frontend.domain.repository.performances.PerformanceRepository

class PerformanceListViewModel(
    private val performanceRepository: PerformanceRepository
) : ViewModel() {

    private val _performancesState = MutableStateFlow<UiState<List<Performance>>>(UiState.Loading)
    val performancesState: StateFlow<UiState<List<Performance>>> = _performancesState.asStateFlow()

    private val _createPerformanceState = MutableStateFlow<UiState<Unit>?>(null)
    val createPerformanceState: StateFlow<UiState<Unit>?> = _createPerformanceState.asStateFlow()

    fun loadPerformances(profileId: String) {
        viewModelScope.launch {
            _performancesState.value = UiState.Loading
            when (val result = performanceRepository.getPerformances(profileId)) {
                is NetworkResult.Success -> {
                    _performancesState.value = UiState.Success(result.data.performances)
                }
                is NetworkResult.Failure -> {
                    _performancesState.value = UiState.Error(
                        UiError.StringRes(result.error.asStringResource())
                    )
                }
            }
        }
    }

    fun createPerformance(profileId: String, request: CreatePerformanceRequest) {
        viewModelScope.launch {
            _createPerformanceState.value = UiState.Loading
            when (val result = performanceRepository.createPerformance(profileId, request)) {
                is NetworkResult.Success -> {
                    _createPerformanceState.value = UiState.Success(Unit)
                    loadPerformances(profileId)
                }
                is NetworkResult.Failure -> {
                    _createPerformanceState.value = UiState.Error(
                        UiError.StringRes(result.error.asStringResource())
                    )
                }
            }
        }
    }

    fun resetCreatePerformanceState() {
        _createPerformanceState.value = null
    }
}
