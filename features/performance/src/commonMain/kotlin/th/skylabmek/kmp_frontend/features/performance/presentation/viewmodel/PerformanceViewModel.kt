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
import th.skylabmek.kmp_frontend.domain.model.performances.PerformanceContentResult
import th.skylabmek.kmp_frontend.domain.repository.performances.PerformanceRepository

class PerformanceViewModel(
    private val performanceRepository: PerformanceRepository
) : ViewModel() {

    private val _performanceContentState = MutableStateFlow<UiState<PerformanceContentResult>>(UiState.Loading)
    val performanceContentState: StateFlow<UiState<PerformanceContentResult>> = _performanceContentState.asStateFlow()

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
}
