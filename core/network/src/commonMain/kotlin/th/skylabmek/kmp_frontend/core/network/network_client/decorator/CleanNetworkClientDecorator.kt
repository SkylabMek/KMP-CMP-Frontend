package th.skylabmek.kmp_frontend.core.network.network_client.decorator

import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.content.OutgoingContent
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import th.skylabmek.kmp_frontend.core.network.client.HttpClientProvider
import th.skylabmek.kmp_frontend.core.network.network_client.NetworkClient
import th.skylabmek.kmp_frontend.core.network.request.RequestSpec
import th.skylabmek.kmp_frontend.core.network.result.NetworkError
import th.skylabmek.kmp_frontend.core.network.result.NetworkResult
import th.skylabmek.kmp_frontend.core.network.result.toNetworkError

/**
 * A decorator that provides a clean execution path, bypassing the standard
 * CommonDefaultKtorApiClient logic (like Base URL and Auth headers).
 */
class CleanNetworkClientDecorator(
    override val httpClientProvider: HttpClientProvider,
    private val isDebug: Boolean = false
) : NetworkClient {

    private val dynamicHeaders = mutableMapOf<String, String>()

    override suspend fun <T> execute(
        reqSpec: RequestSpec,
        mapper: suspend (HttpResponse) -> T
    ): NetworkResult<T> {
        return try {
            val response = httpClientProvider.getHttpClient().request {
                method = reqSpec.method
                // Use path directly as full URL
                url(reqSpec.path)

                headers.appendAll(Headers.build {
                    dynamicHeaders.forEach { (k, v) -> append(k, v) }
                    reqSpec.headers.forEach { (k, v) -> append(k, v) }
                })

                reqSpec.body?.let { body ->
                    if (body !is OutgoingContent) {
                        contentType(ContentType.Application.Json)
                    }
                    setBody(body)
                }
            }

            if (isDebug) {
                println("--- Clean Network Request ---")
                println("Method: ${reqSpec.method.value}")
                println("URL: ${reqSpec.path}")
                println("--- Network Response ---")
                println("Status: ${response.status}")
            }

            if (response.status.isSuccess()) {
                val result = mapper(response)
                NetworkResult.Success(result)
            } else {
                val errorBody = response.bodyAsText()
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