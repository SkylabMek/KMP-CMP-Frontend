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
import th.skylabmek.kmp_frontend.domain.model.performances.PerformanceContentUpdateResult
import th.skylabmek.kmp_frontend.domain.model.performances.UpdatePerformanceContentRequest
import th.skylabmek.kmp_frontend.domain.repository.performances.PerformanceRepository
import th.skylabmek.kmp_frontend.features.feature.markdown.model.MarkdownImage

class PerformanceEditorViewModel(
    private val performanceRepository: PerformanceRepository
) : ViewModel() {

    private val _imageState = MutableStateFlow<UiState<List<UiState<MarkdownImage>>>>(UiState.Loading)
    val imageState: StateFlow<UiState<List<UiState<MarkdownImage>>>> = _imageState.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

    private val _saveState = MutableStateFlow<UiState<PerformanceContentUpdateResult>?>(null)
    val saveState: StateFlow<UiState<PerformanceContentUpdateResult>?> = _saveState.asStateFlow()

    fun loadImages(profileId: String) {
        viewModelScope.launch {
            _imageState.value = UiState.Loading
            when (val result = performanceRepository.getImages(profileId)) {
                is NetworkResult.Success -> {
                    val markdownImages = result.data.images.map { img ->
                        UiState.Success(
                            MarkdownImage(
                                imageId = img.id,
                                filename = img.filename,
                                imageUrl = img.storageUrl
                            )
                        )
                    }
                    _imageState.value = UiState.Success(markdownImages)
                }
                is NetworkResult.Failure -> {
                    _imageState.value = UiState.Error(
                        UiError.StringRes(result.error.asStringResource())
                    )
                }
            }
        }
    }

    fun uploadImage(
        profileId: String,
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String
    ) {
        viewModelScope.launch {
            _isUploading.value = true
            when (val result = performanceRepository.uploadImage(profileId, fileBytes, fileName, mimeType)) {
                is NetworkResult.Success -> {
                    // Refresh images after successful upload
                    loadImages(profileId)
                }
                is NetworkResult.Failure -> {
                    // Handle error (optionally update a separate error state)
                }
            }
            _isUploading.value = false
        }
    }

    fun deleteImage(profileId: String, imageId: String) {
        viewModelScope.launch {
            when (val result = performanceRepository.forceDeleteImage(profileId, imageId)) {
                is NetworkResult.Success -> {
                    val currentState = _imageState.value
                    if (currentState is UiState.Success) {
                        val updatedList = currentState.data.filter { state ->
                            if (state is UiState.Success) {
                                state.data.imageId != imageId
                            } else {
                                true
                            }
                        }
                        _imageState.value = UiState.Success(updatedList)
                    }
                }
                is NetworkResult.Failure -> {
                    // Handle error
                }
            }
        }
    }

    fun saveContent(profileId: String, performanceId: String, content: String) {
        viewModelScope.launch {
            _saveState.value = UiState.Loading
            val request = UpdatePerformanceContentRequest(contentMarkdown = content)
            when (val result = performanceRepository.updatePerformanceContent(profileId, performanceId, request)) {
                is NetworkResult.Success -> {
                    _saveState.value = UiState.Success(result.data)
                }
                is NetworkResult.Failure -> {
                    _saveState.value = UiState.Error(
                        UiError.StringRes(result.error.asStringResource())
                    )
                }
            }
        }
    }
    
    fun resetSaveState() {
        _saveState.value = null
    }
}
