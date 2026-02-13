package th.skylabmek.kmp_frontend.domain.data.repository.auth

import io.ktor.http.HttpMethod
import th.skylabmek.kmp_frontend.core.data_local.domain.LocalSettingsRepository
import th.skylabmek.kmp_frontend.core.network.network_client.NetworkClient
import th.skylabmek.kmp_frontend.core.network.network_client.executeWrapped
import th.skylabmek.kmp_frontend.core.network.request.RequestSpec
import th.skylabmek.kmp_frontend.core.network.result.NetworkResult
import th.skylabmek.kmp_frontend.domain.model.auth.*
import th.skylabmek.kmp_frontend.domain.repository.auth.AuthRepository
import kotlin.time.Clock

class AuthRepositoryImpl(
    private val networkClient: NetworkClient,
    private val localSettings: LocalSettingsRepository
) : AuthRepository {

    override suspend fun login(request: LoginRequest): NetworkResult<TokenResult> {
        val result = networkClient.executeWrapped<TokenResult>(
            reqSpec = RequestSpec(
                method = HttpMethod.Post,
                path = "/auth/login",
                body = request
            )
        )
        if (result is NetworkResult.Success) {
            saveTokens(result.data)
        }
        return result
    }

    override suspend fun refresh(request: RefreshRequest): NetworkResult<TokenResult> {
        val result = networkClient.executeWrapped<TokenResult>(
            reqSpec = RequestSpec(
                method = HttpMethod.Post,
                path = "/auth/refresh",
                body = request
            )
        )
        if (result is NetworkResult.Success) {
            saveTokens(result.data)
        }
        return result
    }

    override suspend fun logout(request: LogoutRequest): NetworkResult<MessageResult> {
        val result = networkClient.executeWrapped<MessageResult>(
            reqSpec = RequestSpec(
                method = HttpMethod.Post,
                path = "/auth/logout",
                body = request
            )
        )
        clearTokens()
        return result
    }

    override suspend fun me(): NetworkResult<MeResult> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Get,
                path = "/auth/me"
            )
        )
    }

    private fun saveTokens(tokenResult: TokenResult) {
        val now = Clock.System.now().toEpochMilliseconds()
        val expiresAt = now + (tokenResult.expiresIn * 1000L)
        
        localSettings.setString("access_token", tokenResult.accessToken)
        localSettings.setString("refresh_token", tokenResult.refreshToken)
        localSettings.setString("expires_at", expiresAt.toString())
        
        tokenResult.refreshTokenExpiresIn?.let { refreshIn ->
            val refreshExpiresAt = now + (refreshIn * 1000L)
            localSettings.setString("refresh_expires_at", refreshExpiresAt.toString())
        }
        
        tokenResult.user?.let { user ->
            localSettings.setString("user_id", user.id)
            localSettings.setString("user_username", user.username)
            localSettings.setString("user_role", user.role)
        }
    }

    private fun clearTokens() {
        localSettings.remove("access_token")
        localSettings.remove("refresh_token")
        localSettings.remove("expires_at")
        localSettings.remove("refresh_expires_at")
        localSettings.remove("user_id")
        localSettings.remove("user_username")
        localSettings.remove("user_role")
    }
}
