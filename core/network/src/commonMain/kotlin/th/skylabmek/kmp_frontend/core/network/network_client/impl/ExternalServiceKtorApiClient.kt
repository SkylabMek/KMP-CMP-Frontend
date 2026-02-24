package th.skylabmek.kmp_frontend.core.network.network_client.impl

import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import io.ktor.http.path
import io.ktor.http.takeFrom
import th.skylabmek.kmp_frontend.core.network.config.NetworkConfig
import th.skylabmek.kmp_frontend.core.network.client.HttpClientProvider
import th.skylabmek.kmp_frontend.core.network.network_client.NetworkClient
import th.skylabmek.kmp_frontend.core.network.request.RequestSpec
import th.skylabmek.kmp_frontend.core.network.result.NetworkError
import th.skylabmek.kmp_frontend.core.network.result.NetworkResult
import th.skylabmek.kmp_frontend.core.network.result.toNetworkError

/**
 * Client for external APIs (e.g., Google Maps, Payment Gateways).
 * It ignores global app headers and focuses on the specific config provided for the external service.
 */
class ExternalServiceKtorApiClient(
    override val httpClientProvider: HttpClientProvider,
    private val serviceConfig: NetworkConfig
) : NetworkClient {

    override suspend fun <T> execute(
        reqSpec: RequestSpec,
        mapper: suspend (HttpResponse) -> T
    ): NetworkResult<T> {
        return try {
            val response = httpClientProvider.getHttpClient().request {
                method = reqSpec.method
                url {
                    // Uses the full URL from the service config
                    takeFrom(serviceConfig.baseUrl)
                    path(reqSpec.path)
                }
                // Only uses headers specific to this service
                headers.appendAll(io.ktor.http.Headers.build {
                    serviceConfig.defaultHeaders.forEach { (k, v) -> append(k, v) }
                    reqSpec.headers.forEach { (k, v) -> append(k, v) }
                })
                reqSpec.body?.let { setBody(it) }
            }

            if (response.status.isSuccess()) {
                NetworkResult.Success(mapper(response))
            } else {
                NetworkResult.Failure(NetworkError.Http(response.status.value, response.status.description))
            }
        } catch (e: Throwable) {
            NetworkResult.Failure(e.toNetworkError())
        }
    }

    override fun setHeader(key: String, value: String) {
        // Not implemented for external service client
    }

    override fun removeHeader(key: String) {
        // Not implemented for external service client
    }
}
