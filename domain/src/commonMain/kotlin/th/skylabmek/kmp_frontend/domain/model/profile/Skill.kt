package th.skylabmek.kmp_frontend.domain.model.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Skill(
    val id: String,
    val name: String,
    @SerialName("skill_type") val skillType: String,
    @SerialName("scale_id") val scaleId: String,
    @SerialName("scale_value") val scaleValue: Double,
    @SerialName("logo_url") val logoUrl: String? = null,
    val description: String? = null
)
