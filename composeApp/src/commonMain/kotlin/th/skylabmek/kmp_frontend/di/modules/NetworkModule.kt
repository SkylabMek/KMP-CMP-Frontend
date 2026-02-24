package th.skylabmek.kmp_frontend.di.modules

import org.koin.dsl.module
import th.skylabmek.kmp_frontend.core.common.AppConfig
import th.skylabmek.kmp_frontend.core.network.config.NetworkConfig
import th.skylabmek.kmp_frontend.core.network.di.networkModule

/**
 * Network module wrapper for the composeApp.
 * Configure your network settings here.
 */
val appNetworkModule = module {
    includes(
        networkModule(
            networkConfig = NetworkConfig(
                baseUrl = AppConfig.apiBaseUrl,
                timeoutMillis = 30000L,
                isDebug = AppConfig.IS_DEBUG
            )
        )
    )
}
