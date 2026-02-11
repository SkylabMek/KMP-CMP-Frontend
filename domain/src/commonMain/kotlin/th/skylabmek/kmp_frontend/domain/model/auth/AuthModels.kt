package th.skylabmek.kmp_frontend.domain.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResult(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") val expiresIn: Int
)

@Serializable
data class MeResult(
    val id: String,
    val username: String,
    val email: String
)

@Serializable
data class MessageResult(
    val message: String,
    val id: String? = null
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class RefreshRequest(
    @SerialName("refresh_token") val refreshToken: String
)

@Serializable
data class LogoutRequest(
    @SerialName("refresh_token") val refreshToken: String
)
