package th.skylabmek.kmp_frontend.domain.repository.auth

import th.skylabmek.kmp_frontend.core.network.result.NetworkResult
import th.skylabmek.kmp_frontend.domain.model.auth.*

interface AuthRepository {
    suspend fun login(request: LoginRequest): NetworkResult<TokenResult>
    suspend fun refresh(request: RefreshRequest): NetworkResult<TokenResult>
    suspend fun logout(request: LogoutRequest): NetworkResult<MessageResult>
    suspend fun me(): NetworkResult<MeResult>
}
