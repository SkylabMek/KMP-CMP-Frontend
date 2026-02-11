package th.skylabmek.kmp_frontend.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.core.data_local.domain.LocalSettingsRepository
import th.skylabmek.kmp_frontend.domain.model.profile.LifeStatus
import th.skylabmek.kmp_frontend.features.profile.presentation.viewmodel.ProfileViewModel
import th.skylabmek.kmp_frontend.ui.config.ThemeSetting

class MainContentViewModel(
    private val profileViewModel: ProfileViewModel,
    private val localSettings: LocalSettingsRepository,
) : ViewModel() {

    private val _lifeStatusState = MutableStateFlow<UiState<LifeStatus>>(UiState.Loading)
    val lifeStatusState: StateFlow<UiState<LifeStatus>> = _lifeStatusState.asStateFlow()

    private val _themeSetting = MutableStateFlow(ThemeSetting.SYSTEM)
    val themeSetting: StateFlow<ThemeSetting> = _themeSetting.asStateFlow()

    init {
        // Observe ProfileViewModel's state and extract LifeStatus
        viewModelScope.launch {
            profileViewModel.uiState.collect { profileUiState ->
                _lifeStatusState.value = when (profileUiState) {
                    is UiState.Loading -> UiState.Loading
                    is UiState.Error -> UiState.Error(profileUiState.uiError)
                    is UiState.Success -> UiState.Success(profileUiState.data.lifeStatus)
                }
            }
        }

        // Observe theme setting changes from local storage
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
    }

    fun setThemeSetting(setting: ThemeSetting) {
        localSettings.setString(THEME_SETTING_KEY, setting.name)
        // Manually update the state to ensure immediate UI feedback
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
