package th.skylabmek.kmp_frontend.features.app_features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import th.skylabmek.kmp_frontend.core.common.UiError
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.core.common.util.isUserLoggedIn
import th.skylabmek.kmp_frontend.core.data_local.domain.LocalSettingsRepository
import th.skylabmek.kmp_frontend.core.network.result.NetworkError
import th.skylabmek.kmp_frontend.core.network.result.NetworkResult
import th.skylabmek.kmp_frontend.domain.model.auth.LoginRequest
import th.skylabmek.kmp_frontend.domain.model.auth.LogoutRequest
import th.skylabmek.kmp_frontend.domain.model.auth.MeResult
import th.skylabmek.kmp_frontend.domain.model.performances.PerformanceListResult
import th.skylabmek.kmp_frontend.domain.model.profile.Announce
import th.skylabmek.kmp_frontend.domain.model.profile.LifeStatus
import th.skylabmek.kmp_frontend.domain.model.profile.Profile
import th.skylabmek.kmp_frontend.domain.repository.auth.AuthRepository
import th.skylabmek.kmp_frontend.domain.repository.profile.ProfileRepository
import th.skylabmek.kmp_frontend.features.app_features.profile.presentation.ui.profile.LoginState
import th.skylabmek.kmp_frontend.shared_resources.Res
import th.skylabmek.kmp_frontend.shared_resources.error_unknown

data class ProfileBasicData(
    val lifeStatus: LifeStatus,
    val announces: List<Announce>,
    val profile: Profile? = null
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
    private val localSettings: LocalSettingsRepository
) : ViewModel() {
    private var hasBasicDataLoadedOnce = false
    private var hasPerformancesLoadedOnce = false

    private val _uiState = MutableStateFlow<UiState<ProfileBasicData>>(UiState.Loading)
    val uiState: StateFlow<UiState<ProfileBasicData>> = _uiState.asStateFlow()

    private val _performancesState = MutableStateFlow<UiState<PerformanceListResult>>(UiState.Loading)
    val performancesState: StateFlow<UiState<PerformanceListResult>> = _performancesState.asStateFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _meState = MutableStateFlow<UiState<MeResult>>(UiState.Loading)
    val meState: StateFlow<UiState<MeResult>> = _meState.asStateFlow()

    init {
        if (isUserLoggedIn(localSettings)) {
            loadMe()
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val result = authRepository.login(LoginRequest(username, password))
            when (result) {
                is NetworkResult.Success -> {
                    _loginState.value = LoginState.Success
                    loadMe()
                }
                is NetworkResult.Failure -> {
                    _loginState.value = LoginState.Error(result.error.toString())
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val refreshToken = localSettings.getString("refresh_token", "")
            authRepository.logout(LogoutRequest(refreshToken = refreshToken))
            _meState.value = UiState.Error(UiError.StringRes(Res.string.error_unknown))
        }
    }

    fun loadMe() {
        viewModelScope.launch {
            _meState.value = UiState.Loading
            when (val result = authRepository.me()) {
                is NetworkResult.Success -> {
                    _meState.value = UiState.Success(result.data)
                }
                is NetworkResult.Failure -> {
                    _meState.value = UiState.Error(
                        UiError.StringRes(result.error.asStringResource())
                    )
                }
            }
        }
    }

    fun getOrLoadProfileBasicData(profileId: String): StateFlow<UiState<ProfileBasicData>> {
        if (!hasBasicDataLoadedOnce) {
            hasBasicDataLoadedOnce = true
            loadProfileBasicData(profileId)
        }
        return uiState
    }

    fun getOrLoadPerformances(profileId: String): StateFlow<UiState<PerformanceListResult>> {
        if (!hasPerformancesLoadedOnce) {
            hasPerformancesLoadedOnce = true
            loadPerformances(profileId)
        }
        return performancesState
    }

    fun loadProfileBasicData(profileId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            val lifeStatusResult = profileRepository.getLifeStatus(profileId)
            val announcesResult = profileRepository.getAnnounces(profileId)
            val profileResult = profileRepository.getProfile(profileId)

            if (lifeStatusResult is NetworkResult.Success && 
                announcesResult is NetworkResult.Success &&
                profileResult is NetworkResult.Success) {
                _uiState.value = UiState.Success(
                    ProfileBasicData(
                        lifeStatus = lifeStatusResult.data,
                        announces = announcesResult.data.items,
                        profile = profileResult.data.profile
                    )
                )
            } else {
                val networkError = when {
                    lifeStatusResult is NetworkResult.Failure -> lifeStatusResult.error
                    announcesResult is NetworkResult.Failure -> announcesResult.error
                    profileResult is NetworkResult.Failure -> profileResult.error
                    else -> NetworkError.Unknown(Throwable("Unknown error"))
                }
                _uiState.value = UiState.Error(
                    UiError.StringRes(networkError.asStringResource())
                )
            }
        }
    }

    fun loadPerformances(profileId: String) {
        viewModelScope.launch {
            _performancesState.value = UiState.Loading
            
            when (val result = profileRepository.getPublicPerformances(profileId)) {
                is NetworkResult.Success -> {
                    _performancesState.value = UiState.Success(result.data)
//                    println("data res is : " + result.data)
                }
                is NetworkResult.Failure -> {
                    _performancesState.value = UiState.Error(
                        UiError.StringRes(result.error.asStringResource())
                    )
                    println("loadPerformances Failure with error: ${result.error}")
                }
            }
        }
    }
}
