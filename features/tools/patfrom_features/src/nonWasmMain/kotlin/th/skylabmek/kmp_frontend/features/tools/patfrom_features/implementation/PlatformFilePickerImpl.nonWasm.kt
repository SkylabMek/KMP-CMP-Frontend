package th.skylabmek.kmp_frontend.features.tools.patfrom_features.implementation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import th.skylabmek.kmp_frontend.features.tools.patfrom_features.domain.service.PlatformFilePicker

class NonWasmPlatformFilePicker : BasePlatformFilePicker()

@Composable
actual fun rememberPlatformFilePicker(): PlatformFilePicker {
    return remember { NonWasmPlatformFilePicker() }
}

@Composable
actual fun PlatformFilePickerHandler(picker: PlatformFilePicker) {
    val nonWasmPicker = picker as NonWasmPlatformFilePicker
    val scope = rememberCoroutineScope()
    
    FilePicker(
        show = nonWasmPicker.showPicker,
        fileExtensions = nonWasmPicker.allowedExtensions
    ) { platformFile ->
        scope.launch(Dispatchers.Default) {
            if (platformFile != null) {
                val bytes = platformFile.getFileByteArray()
                val path = platformFile.path
                val fileName = path.substringAfterLast('/')
                val extension = fileName.substringAfterLast('.', "")
                
                val result = th.skylabmek.kmp_frontend.features.tools.patfrom_features.domain.model.PlatformFile(
                    name = fileName,
                    bytes = bytes,
                    mimeType = nonWasmPicker.getMimeTypeFromExtension(extension)
                )
                nonWasmPicker.onFilePicked?.complete(result)
            } else {
                nonWasmPicker.onFilePicked?.complete(null)
            }
            nonWasmPicker.showPicker = false
        }
    }
}
