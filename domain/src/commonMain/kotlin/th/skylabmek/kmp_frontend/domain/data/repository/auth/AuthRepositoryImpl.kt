package th.skylabmek.kmp_frontend.domain.data.repository.auth

import io.ktor.http.HttpMethod
import th.skylabmek.kmp_frontend.core.data_local.domain.LocalSettingsRepository
import th.skylabmek.kmp_frontend.core.network.network_client.NetworkClient
import th.skylabmek.kmp_frontend.core.network.network_client.executeWrapped
import th.skylabmek.kmp_frontend.core.network.request.RequestSpec
import th.skylabmek.kmp_frontend.core.network.result.NetworkResult
import th.skylabmek.kmp_frontend.domain.model.auth.*
import th.skylabmek.kmp_frontend.domain.repository.auth.AuthRepository

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
            localSettings.setString("access_token", result.data.accessToken)
            localSettings.setString("refresh_token", result.data.refreshToken)
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
            localSettings.setString("access_token", result.data.accessToken)
            localSettings.setString("refresh_token", result.data.refreshToken)
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
        localSettings.remove("access_token")
        localSettings.remove("refresh_token")
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
}
