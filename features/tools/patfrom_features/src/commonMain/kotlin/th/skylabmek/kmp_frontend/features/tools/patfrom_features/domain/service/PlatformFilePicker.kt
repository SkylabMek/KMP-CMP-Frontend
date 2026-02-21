package th.skylabmek.kmp_frontend.features.tools.patfrom_features.domain.service

import th.skylabmek.kmp_frontend.features.tools.patfrom_features.domain.model.PlatformFile

interface PlatformFilePicker {
    suspend fun pickImage(): PlatformFile?
    suspend fun pickFile(allowedExtensions: List<String>): PlatformFile?
}
