package th.skylabmek.kmp_frontend.core.network.di


import org.koin.core.module.Module
import org.koin.dsl.module
import th.skylabmek.kmp_frontend.core.network.config.NetworkConfig
import th.skylabmek.kmp_frontend.core.network.factory.HttpClientProviderFactory
import th.skylabmek.kmp_frontend.core.network.factory.NetworkClientFactory
import th.skylabmek.kmp_frontend.core.network.network_client.NetworkClient

fun networkModule(
    networkConfig: NetworkConfig
): Module = module {

    // 1. Core Config & Factories
    single { networkConfig }
    single { HttpClientProviderFactory(get()) }
    single { NetworkClientFactory(get(), get()) }

    // 2. Default Feature Client (Shared)
    single<NetworkClient>(qualifier = NetworkQualifier.Default.qualifier) {
        get<NetworkClientFactory>().createDefaultFeatureNetworkClient(get())
    }

    // 3. Fatal Error Logging Client (Stateless)
    single<NetworkClient>(qualifier = NetworkQualifier.Logged.qualifier) {
        get<NetworkClientFactory>().createErrorFatalLogNetworkClient()
    }
}
