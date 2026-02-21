package th.skylabmek.kmp_frontend.features.tools.patfrom_features.implementation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CompletableDeferred
import th.skylabmek.kmp_frontend.features.tools.patfrom_features.domain.model.PlatformFile
import th.skylabmek.kmp_frontend.features.tools.patfrom_features.domain.service.PlatformFilePicker

abstract class BasePlatformFilePicker : PlatformFilePicker {
    internal var onFilePicked: CompletableDeferred<PlatformFile?>? = null
    
    var showPicker by mutableStateOf(false)
    var allowedExtensions by mutableStateOf(listOf<String>())

    override suspend fun pickImage(): PlatformFile? {
        return pickFile(listOf("jpg", "jpeg", "png", "webp"))
    }

    override suspend fun pickFile(allowedExtensions: List<String>): PlatformFile? {
        val deferred = CompletableDeferred<PlatformFile?>()
        onFilePicked = deferred
        this.allowedExtensions = allowedExtensions
        showPicker = true
        
        return try {
            deferred.await()
        } finally {
            showPicker = false
            onFilePicked = null
        }
    }

    internal fun getMimeTypeFromExtension(extension: String): String {
        return when (extension.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            "pdf" -> "application/pdf"
            else -> "application/octet-stream"
        }
    }
}
