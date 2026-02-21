package th.skylabmek.kmp_frontend.features.tools.patfrom_features.implementation

import androidx.compose.runtime.Composable
import th.skylabmek.kmp_frontend.features.tools.patfrom_features.domain.service.PlatformFilePicker

@Composable
expect fun rememberPlatformFilePicker(): PlatformFilePicker

@Composable
expect fun PlatformFilePickerHandler(picker: PlatformFilePicker)
