package th.skylabmek.kmp_frontend.core.network.request

import io.ktor.http.HttpMethod

enum class HttpMethodReq(val value: HttpMethod) {
    GET(HttpMethod.Get),
    POST(HttpMethod.Post),
    PUT(HttpMethod.Put),
    DELETE(HttpMethod.Delete)
}

data class RequestSpec(
    val method: HttpMethod,
    val path: String,
    val headers: Map<String, String> = emptyMap(),
    val body: Any? = null
)
