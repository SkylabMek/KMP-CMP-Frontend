package th.skylabmek.kmp_frontend.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.core.common.util.isRefreshTokenExpired
import th.skylabmek.kmp_frontend.core.common.util.shouldRefreshToken
import th.skylabmek.kmp_frontend.core.data_local.domain.LocalSettingsRepository
import th.skylabmek.kmp_frontend.domain.model.auth.RefreshRequest
import th.skylabmek.kmp_frontend.domain.model.profile.LifeStatus
import th.skylabmek.kmp_frontend.domain.repository.auth.AuthRepository
import th.skylabmek.kmp_frontend.features.app_features.profile.presentation.viewmodel.ProfileViewModel
import th.skylabmek.kmp_frontend.ui.config.ThemeSetting
import kotlin.time.Clock

class MainContentViewModel(
    private val profileViewModel: ProfileViewModel,
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

        // 2. Observe ProfileViewModel's state and extract LifeStatus
        viewModelScope.launch {
            profileViewModel.uiState.collect { profileUiState ->
                _lifeStatusState.value = when (profileUiState) {
                    is UiState.Loading -> UiState.Loading
                    is UiState.Error -> UiState.Error(profileUiState.uiError)
                    is UiState.Success -> UiState.Success(profileUiState.data.lifeStatus)
                }
            }
        }

        // 3. Observe theme setting changes from local storage
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

        // 4. Reactive Token Refresh Countdown
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
        profileViewModel.loadProfileBasicData(profileId)
    }

    fun refreshLifeStatus(profileId: String) {
        profileViewModel.loadProfileBasicData(profileId)
    }

    companion object {
        private const val THEME_SETTING_KEY = "theme_setting"
    }
}
