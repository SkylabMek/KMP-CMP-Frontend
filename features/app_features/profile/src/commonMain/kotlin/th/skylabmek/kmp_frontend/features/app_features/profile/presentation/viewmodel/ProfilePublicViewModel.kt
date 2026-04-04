package th.skylabmek.kmp_frontend.features.app_features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import th.skylabmek.kmp_frontend.core.common.UiError
import th.skylabmek.kmp_frontend.core.common.UiState
import th.skylabmek.kmp_frontend.core.network.result.NetworkResult
import th.skylabmek.kmp_frontend.domain.model.profile.ProfileResult
import th.skylabmek.kmp_frontend.domain.repository.profile.ProfileRepository

class ProfilePublicViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _profileState = MutableStateFlow<UiState<ProfileResult>>(UiState.Loading)
    val profileState: StateFlow<UiState<ProfileResult>> = _profileState.asStateFlow()

    fun loadPublicProfile(profileId: String) {
        viewModelScope.launch {
            _profileState.value = UiState.Loading
            when (val result = profileRepository.getPublicProfile(profileId)) {
                is NetworkResult.Success -> {
                    _profileState.value = UiState.Success(result.data)
                }
                is NetworkResult.Failure -> {
                    _profileState.value = UiState.Error(
                        UiError.StringRes(result.error.asStringResource())
                    )
                }
            }
        }
    }
}
