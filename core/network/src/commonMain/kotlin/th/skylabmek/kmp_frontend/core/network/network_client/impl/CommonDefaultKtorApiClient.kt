package th.skylabmek.kmp_frontend.core.network.network_client.impl

import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.http.takeFrom
import io.ktor.http.content.OutgoingContent
import th.skylabmek.kmp_frontend.core.data_local.domain.LocalSettingsRepository
import th.skylabmek.kmp_frontend.core.network.config.NetworkConfig
import th.skylabmek.kmp_frontend.core.network.client.HttpClientProvider
import th.skylabmek.kmp_frontend.core.network.network_client.NetworkClient
import th.skylabmek.kmp_frontend.core.network.request.RequestSpec
import th.skylabmek.kmp_frontend.core.network.result.NetworkError
import th.skylabmek.kmp_frontend.core.network.result.NetworkResult
import th.skylabmek.kmp_frontend.core.network.result.toNetworkError

class CommonDefaultKtorApiClient(
    private val httpClientProvider: HttpClientProvider,
    private val networkConfig: NetworkConfig,
    private val localSettings: LocalSettingsRepository
) : NetworkClient {

    private val dynamicHeaders = mutableMapOf<String, String>()

    override suspend fun <T> execute(
        reqSpec: RequestSpec,
        mapper: suspend (HttpResponse) -> T
    ): NetworkResult<T> {
        return try {
            val response = httpClientProvider.getHttpClient().request {
                method = reqSpec.method
                url {
                    takeFrom(networkConfig.baseUrl)
                    val path = if (reqSpec.path.startsWith("/")) reqSpec.path else "/${reqSpec.path}"
                    encodedPath = path
                }

                val token = localSettings.getString("access_token", "")

                headers.appendAll(io.ktor.http.Headers.build {
                    networkConfig.defaultHeaders.forEach { (k, v) -> append(k, v) }
                    dynamicHeaders.forEach { (k, v) -> append(k, v) }
                    reqSpec.headers.forEach { (k, v) -> append(k, v) }
                    if (token.isNotEmpty()) {
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                })
                
                reqSpec.body?.let { body ->
                    if (body !is OutgoingContent) {
                        contentType(ContentType.Application.Json)
                    }
                    setBody(body)
                }
            }

            if (response.status.isSuccess()) {
                NetworkResult.Success(mapper(response))
            } else {
                NetworkResult.Failure(
                    NetworkError.Http(
                        code = response.status.value,
                        message = response.status.description
                    )
                )
            }

        } catch (e: Throwable) {
            NetworkResult.Failure(e.toNetworkError())
        }
    }

    override fun setHeader(key: String, value: String) {
        dynamicHeaders[key] = value
    }

    override fun removeHeader(key: String) {
        dynamicHeaders.remove(key)
    }
}
