package th.skylabmek.kmp_frontend.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import th.skylabmek.kmp_frontend.core.common.UiError
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.core.common.util.isRefreshTokenExpired
import th.skylabmek.kmp_frontend.core.common.util.shouldRefreshToken
import th.skylabmek.kmp_frontend.core.data_local.domain.LocalSettingsRepository
import th.skylabmek.kmp_frontend.core.network.result.NetworkResult
import th.skylabmek.kmp_frontend.domain.model.auth.RefreshRequest
import th.skylabmek.kmp_frontend.domain.model.profile.LifeStatus
import th.skylabmek.kmp_frontend.domain.repository.auth.AuthRepository
import th.skylabmek.kmp_frontend.domain.repository.profile.ProfileRepository
import th.skylabmek.kmp_frontend.features.app_features.profile.presentation.viewmodel.ProfileViewModel
import th.skylabmek.kmp_frontend.ui.config.ThemeSetting
import kotlin.time.Clock

class MainContentViewModel(
    private val profileViewModel: ProfileViewModel,
    private val profileRepository: ProfileRepository,
    private val localSettings: LocalSettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _lifeStatusState = MutableStateFlow<UiState<LifeStatus>>(UiState.Loading)
    val lifeStatusState: StateFlow<UiState<LifeStatus>> = _lifeStatusState.asStateFlow()

    private val _themeSetting = MutableStateFlow(ThemeSetting.SYSTEM)
    val themeSetting: StateFlow<ThemeSetting> = _themeSetting.asStateFlow()

    init {
        // 1. Initial Check: If the refresh token itself is expired, clear everything immediately
        checkInitialAuthStatus()

        // 2. Observe theme setting changes from local storage
        viewModelScope.launch {
            localSettings.getStringFlow(THEME_SETTING_KEY, ThemeSetting.SYSTEM.name)
                .collectLatest { settingName ->
                    try {
                        _themeSetting.value = ThemeSetting.valueOf(settingName)
                    } catch (e: Exception) {
                        _themeSetting.value = ThemeSetting.SYSTEM
                    }
                }
        }

        // 3. Reactive Token Refresh Countdown
        startTokenRefreshTimer()
    }

    private fun checkInitialAuthStatus() {
        if (isRefreshTokenExpired(localSettings)) {
            // Refresh token is dead, user must log in again
            localSettings.clear() 
        }
    }

    private fun startTokenRefreshTimer() {
        viewModelScope.launch {
            localSettings.getStringFlow("expires_at", "0").collectLatest { expiresAtStr ->
                val expiresAt = expiresAtStr.toLongOrNull() ?: 0L
                if (expiresAt <= 0) return@collectLatest

                val refreshThreshold = 5 * 60 * 1000L // 5 minutes buffer
                val currentTime = Clock.System.now().toEpochMilliseconds()
                val delayTime = (expiresAt - currentTime) - refreshThreshold

                if (delayTime > 0) {
                    delay(delayTime)
                }

                if (shouldRefreshToken(localSettings)) {
                    val refreshToken = localSettings.getString("refresh_token", "")
                    if (refreshToken.isNotEmpty()) {
                        authRepository.refresh(RefreshRequest(refreshToken))
                    }
                }
            }
        }
    }

    fun setThemeSetting(setting: ThemeSetting) {
        localSettings.setString(THEME_SETTING_KEY, setting.name)
        _themeSetting.value = setting
    }

    fun loadProfileData(profileId: String) {
        // Load profile basic data (profile, announces, lifeStatus) into ProfileViewModel
        profileViewModel.loadProfileBasicData(profileId)
        
        // Load lifeStatus separately for MainContent's lifeStatusState
        loadLifeStatus(profileId)
    }

    private fun loadLifeStatus(profileId: String) {
        viewModelScope.launch {
            _lifeStatusState.value = UiState.Loading
            when (val result = profileRepository.getLifeStatus(profileId)) {
                is NetworkResult.Success -> {
                    _lifeStatusState.value = UiState.Success(result.data)
                }
                is NetworkResult.Failure -> {
                    _lifeStatusState.value = UiState.Error(
                        UiError.StringRes(result.error.asStringResource())
                    )
                }
            }
        }
    }

    fun refreshLifeStatus(profileId: String) {
        loadLifeStatus(profileId)
    }

    companion object {
        private const val THEME_SETTING_KEY = "theme_setting"
    }
}
