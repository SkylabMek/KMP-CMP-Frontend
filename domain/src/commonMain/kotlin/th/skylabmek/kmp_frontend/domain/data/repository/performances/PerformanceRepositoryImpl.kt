package th.skylabmek.kmp_frontend.domain.data.repository.performances

import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import th.skylabmek.kmp_frontend.core.network.network_client.NetworkClient
import th.skylabmek.kmp_frontend.core.network.network_client.executeWrapped
import th.skylabmek.kmp_frontend.core.network.network_client.decorator.CleanNetworkClientDecorator
import th.skylabmek.kmp_frontend.core.network.request.RequestSpec
import th.skylabmek.kmp_frontend.core.network.result.NetworkResult
import th.skylabmek.kmp_frontend.domain.model.performances.*
import th.skylabmek.kmp_frontend.domain.repository.performances.PerformanceRepository

class PerformanceRepositoryImpl(
    private val networkClient: NetworkClient
) : PerformanceRepository {

    override suspend fun getPerformances(profileId: String): NetworkResult<PerformanceListResult> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Get,
                path = "/profiles/$profileId/performances"
            )
        )
    }

    override suspend fun getPublicPerformances(profileId: String): NetworkResult<PerformanceListResult> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Get,
                path = "/profiles/$profileId/publicPerformances"
            )
        )
    }

    override suspend fun createPerformance(
        profileId: String,
        request: CreatePerformanceRequest
    ): NetworkResult<PerformanceResult> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Post,
                path = "/profiles/$profileId/performances",
                body = request
            )
        )
    }

    override suspend fun updatePerformance(
        profileId: String,
        performanceId: String,
        request: UpdatePerformanceRequest
    ): NetworkResult<PerformanceUpdateResult> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Patch,
                path = "/profiles/$profileId/performances/$performanceId",
                body = request
            )
        )
    }

    override suspend fun deletePerformance(
        profileId: String,
        performanceId: String
    ): NetworkResult<PerformanceDeleteResult> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Delete,
                path = "/profiles/$profileId/performances/$performanceId"
            )
        )
    }

    override suspend fun getPerformanceContent(
        profileId: String,
        performanceId: String
    ): NetworkResult<PerformanceContentResult> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Get,
                path = "/profiles/$profileId/performances/$performanceId/content"
            )
        )
    }

    override suspend fun getPerformanceContentFromUrl(url: String): NetworkResult<String> {
        // Create a clean decorator on the fly using the shared provider from the main client.
        // This bypasses CommonDefaultKtorApiClient's BaseURL and Header logic.
        val cleanClient = CleanNetworkClientDecorator(networkClient.httpClientProvider)
        
        return cleanClient.execute(
            reqSpec = RequestSpec(
                method = HttpMethod.Get,
                path = url
            ),
            mapper = { it.bodyAsText() }
        )
    }

    override suspend fun updatePerformanceContent(
        profileId: String,
        performanceId: String,
        request: UpdatePerformanceContentRequest
    ): NetworkResult<PerformanceContentUpdateResult> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Patch,
                path = "/profiles/$profileId/performances/$performanceId/content",
                body = request
            )
        )
    }

    override suspend fun getImages(
        profileId: String,
        search: String?,
        limit: Int?,
        offset: Int?
    ): NetworkResult<ImageListResult> {
        var pathWithParams = "/profiles/$profileId/images"
        val params = mutableListOf<String>()
        search?.let { params.add("search=$it") }
        limit?.let { params.add("limit=$it") }
        offset?.let { params.add("offset=$it") }

        if (params.isNotEmpty()) {
            pathWithParams += "?" + params.joinToString("&")
        }

        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Get,
                path = pathWithParams
            )
        )
    }

    override suspend fun uploadImage(
        profileId: String,
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        altText: String?,
        caption: String?
    ): NetworkResult<ImageResult> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Post,
                path = "/profiles/$profileId/images",
                body = MultiPartFormDataContent(
                    formData {
                        append("file", fileBytes, Headers.build {
                            append(HttpHeaders.ContentType, mimeType)
                            append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                        })
                        altText?.let { append("alt_text", it) }
                        caption?.let { append("caption", it) }
                    }
                )
            )
        )
    }

    override suspend fun forceDeleteImage(profileId: String, imageId: String): NetworkResult<MessageResult> {
        return networkClient.executeWrapped(
            reqSpec = RequestSpec(
                method = HttpMethod.Delete,
                path = "/profiles/$profileId/images/$imageId/force"
            )
        )
    }
}
