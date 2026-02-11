package th.skylabmek.kmp_frontend.core.network.network_client

import io.ktor.client.statement.HttpResponse
import th.skylabmek.kmp_frontend.core.network.request.RequestSpec
import th.skylabmek.kmp_frontend.core.network.result.NetworkResult

interface NetworkClient {
    suspend fun <T> execute(
        reqSpec: RequestSpec,
        mapper: suspend (HttpResponse) -> T,
    ): NetworkResult<T>

    fun setHeader(key: String, value: String)
    fun removeHeader(key: String)
}
