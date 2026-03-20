package th.skylabmek.kmp_frontend.domain.model.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Social(
    val id: String,
    val name: String,
    val link: String,
    @SerialName("logo_url") val logoUrl: String? = null
)
