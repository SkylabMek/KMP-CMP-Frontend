package th.skylabmek.kmp_frontend.domain.model.app

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppConfig(
    @SerialName("icon_base_url") val iconBaseUrl: String
)
