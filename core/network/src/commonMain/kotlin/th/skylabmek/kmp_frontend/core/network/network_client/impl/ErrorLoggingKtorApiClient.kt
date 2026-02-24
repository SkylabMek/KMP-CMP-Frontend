package th.skylabmek.kmp_frontend.core.network.network_client.impl

import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import th.skylabmek.kmp_frontend.core.network.config.NetworkConfig
import th.skylabmek.kmp_frontend.core.network.client.HttpClientProvider
import th.skylabmek.kmp_frontend.core.network.network_client.NetworkClient
import th.skylabmek.kmp_frontend.core.network.request.RequestSpec
import th.skylabmek.kmp_frontend.core.network.result.NetworkError
import th.skylabmek.kmp_frontend.core.network.result.NetworkResult

/**
 * Specialized client for sending crash reports and fatal logs.
 * It prioritizes delivery and avoids complex transformations that might fail during a crash.
 */
class ErrorLoggingKtorApiClient(
    override val httpClientProvider: HttpClientProvider,
    private val networkConfig: NetworkConfig
) : NetworkClient {

    override suspend fun <T> execute(
        reqSpec: RequestSpec,
        mapper: suspend (HttpResponse) -> T
    ): NetworkResult<T> {
        return try {
            val response = httpClientProvider.getHttpClient().request(reqSpec.path) {
                method = reqSpec.method
                contentType(ContentType.Application.Json)
                // Add specific headers for logging if needed
                headers.append("X-Log-Priority", "Critical")
                reqSpec.body?.let { setBody(it) }
            }

            if (response.status.isSuccess()) {
                NetworkResult.Success(mapper(response))
            } else {
                NetworkResult.Failure(NetworkError.Http(response.status.value, "Log delivery failed"))
            }
        } catch (e: Throwable) {
            // During fatal logging, we return a simple failure without deep inspection 
            // to avoid further overhead in a crashing state.
            NetworkResult.Failure(NetworkError.Unknown(e))
        }
    }

    override fun setHeader(key: String, value: String) {
        // Not implemented for error logging client
    }

    override fun removeHeader(key: String) {
        // Not implemented for error logging client
    }
}
