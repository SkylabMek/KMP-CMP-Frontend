package th.skylabmek.kmp_frontend.domain.repository.app

import th.skylabmek.kmp_frontend.core.network.result.NetworkResult
import th.skylabmek.kmp_frontend.domain.model.app.AppConfig

interface AppRepository {
    suspend fun getAppConfig(appID: String): NetworkResult<AppConfig>
}
