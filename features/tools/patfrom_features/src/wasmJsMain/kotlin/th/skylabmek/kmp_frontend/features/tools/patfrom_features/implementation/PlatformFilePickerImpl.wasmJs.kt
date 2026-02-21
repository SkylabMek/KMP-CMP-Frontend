package th.skylabmek.kmp_frontend.features.tools.patfrom_features.implementation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.browser.document
import kotlinx.coroutines.launch
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.get
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.files.File
import org.w3c.files.FileReader
import org.w3c.files.get
import th.skylabmek.kmp_frontend.features.tools.patfrom_features.domain.model.PlatformFile
import th.skylabmek.kmp_frontend.features.tools.patfrom_features.domain.service.PlatformFilePicker

class WasmPlatformFilePicker : BasePlatformFilePicker()

@Composable
actual fun rememberPlatformFilePicker(): PlatformFilePicker {
    return remember { WasmPlatformFilePicker() }
}

@Composable
actual fun PlatformFilePickerHandler(picker: PlatformFilePicker) {
    // On WasmJs, we don't need a persistent UI element like mpfilepicker's FilePicker.
    // We trigger the picker manually via the browser API when pickFile is called.
    // However, to keep the same API flow, we observe the showPicker state.
    
    val wasmPicker = picker as WasmPlatformFilePicker
    val scope = rememberCoroutineScope()

    if (wasmPicker.showPicker) {
        val input = document.createElement("input") as HTMLInputElement
        input.type = "file"
        input.accept = wasmPicker.allowedExtensions.joinToString(",") { ".$it" }
        
        input.onchange = { event: Event ->
            val file = input.files?.get(0)
            if (file != null) {
                readFile(file) { bytes ->
                    scope.launch {
                        val platformFile = PlatformFile(
                            name = file.name,
                            bytes = bytes,
                            mimeType = wasmPicker.getMimeTypeFromExtension(file.name.substringAfterLast(".", ""))
                        )
                        wasmPicker.onFilePicked?.complete(platformFile)
                        wasmPicker.showPicker = false
                    }
                }
            } else {
                wasmPicker.onFilePicked?.complete(null)
                wasmPicker.showPicker = false
            }
        }
        
        // Handle cancel
        document.body?.onfocus = {
            // This is a common hack to detect file picker cancel in browsers
            // because there's no native "cancel" event for input[type=file].
            // We wait a bit to see if onchange was triggered.
            scope.launch {
                kotlinx.coroutines.delay(300)
                if (wasmPicker.showPicker) {
                    wasmPicker.onFilePicked?.complete(null)
                    wasmPicker.showPicker = false
                }
            }
        }

        input.click()
    }
}

private fun readFile(file: File, callback: (ByteArray) -> Unit) {
    val reader = FileReader()
    reader.onload = { event ->
        val arrayBuffer = reader.result as ArrayBuffer
        val int8Array = Int8Array(arrayBuffer)
        val bytes = ByteArray(int8Array.length) { i -> int8Array[i] }
        callback(bytes)
    }
    reader.readAsArrayBuffer(file)
}
