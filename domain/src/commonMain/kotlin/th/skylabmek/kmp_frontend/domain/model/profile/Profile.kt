package th.skylabmek.kmp_frontend.domain.model.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    @SerialName("current_status_id") val currentStatusId: String? = null
)

@Serializable
data class ProfileResult(
    val profile: Profile
)
