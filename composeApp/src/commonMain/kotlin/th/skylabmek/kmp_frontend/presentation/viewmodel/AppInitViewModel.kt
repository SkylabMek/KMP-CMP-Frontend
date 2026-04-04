package th.skylabmek.kmp_frontend.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import th.skylabmek.kmp_frontend.core.common.UiError
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.core.common.util.isRefreshTokenExpired
import th.skylabmek.kmp_frontend.core.data_local.domain.LocalSettingsRepository
import th.skylabmek.kmp_frontend.core.network.result.NetworkResult
import th.skylabmek.kmp_frontend.domain.model.app.AppConfig
import th.skylabmek.kmp_frontend.domain.repository.app.AppRepository

class AppInitViewModel(
    private val configRepository: AppRepository,
    private val localSettings: LocalSettingsRepository,
    val appId: String
) : ViewModel() {

    private val _appConfigUiState = MutableStateFlow<UiState<AppConfig>>(UiState.Loading)
    val appConfigUiState: StateFlow<UiState<AppConfig>> = _appConfigUiState.asStateFlow()

    init {
        checkInitialAuthStatus()
        fetchAppConfig()
    }

    private fun checkInitialAuthStatus() {
        if (isRefreshTokenExpired(localSettings)) {
            localSettings.clear()
        }
    }

    private fun fetchAppConfig() {
        viewModelScope.launch {
            _appConfigUiState.value = UiState.Loading
            when (val config = configRepository.getAppConfig(appID = appId)) {
                is NetworkResult.Success -> {
                    _appConfigUiState.value = UiState.Success(config.data)
                }

                is NetworkResult.Failure -> {
                    _appConfigUiState.value = UiState.Error(
                        UiError.StringRes(config.error.asStringResource())
                    )
                }
            }

        }
    }
}