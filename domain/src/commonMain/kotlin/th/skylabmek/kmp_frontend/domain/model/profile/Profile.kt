package th.skylabmek.kmp_frontend.domain.model.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("display_name") val displayName: String,
    val headline: String? = null,
    val bio: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("contact_email") val contactEmail: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("current_status_id") val currentStatusId: String? = null,
    @SerialName("current_status") val currentStatus: LifeStatus? = null
)

@Serializable
data class ProfileResult(
    val profile: Profile,
    val announces: List<Announce> = emptyList(),
    val skills: List<Skill> = emptyList(),
    val socials: List<Social> = emptyList()
)
