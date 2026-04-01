package th.skylabmek.kmp_frontend.domain.data.repository.app

import io.ktor.http.HttpMethod
import th.skylabmek.kmp_frontend.core.network.network_client.NetworkClient
import th.skylabmek.kmp_frontend.core.network.network_client.executeWrapped
import th.skylabmek.kmp_frontend.core.network.request.RequestSpec
import th.skylabmek.kmp_frontend.core.network.result.NetworkResult
import th.skylabmek.kmp_frontend.domain.model.app.AppConfig
import th.skylabmek.kmp_frontend.domain.repository.app.AppRepository

class AppRepositoryImpl(
    private val networkClient: NetworkClient
) : AppRepository {

    override suspend fun getAppConfig(appID: String): NetworkResult<AppConfig> {
        return networkClient.executeWrapped<AppConfig>(
            reqSpec = RequestSpec(
                method = HttpMethod.Get,
                path = "/app/$appID/config"
            )
        )
    }
}
