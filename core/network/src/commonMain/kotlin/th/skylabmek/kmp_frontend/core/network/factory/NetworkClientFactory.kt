package th.skylabmek.kmp_frontend.core.network.factory

import th.skylabmek.kmp_frontend.core.data_local.domain.LocalSettingsRepository
import th.skylabmek.kmp_frontend.core.network.config.NetworkConfig
import th.skylabmek.kmp_frontend.core.network.network_client.impl.CommonDefaultKtorApiClient
import th.skylabmek.kmp_frontend.core.network.network_client.impl.ErrorLoggingKtorApiClient
import th.skylabmek.kmp_frontend.core.network.network_client.impl.ExternalServiceKtorApiClient
import th.skylabmek.kmp_frontend.core.network.network_client.decorator.CleanNetworkClientDecorator
import th.skylabmek.kmp_frontend.core.network.network_client.NetworkClient

class NetworkClientFactory(
    private val defaultNetworkConfig: NetworkConfig,
    private val httpClientProviderFactory: HttpClientProviderFactory
) {
    /**
     * Standard client for feature-related API calls.
     * Uses CommonDefaultKtorApiClient which includes Base URL and Header logic.
     */
    fun createDefaultFeatureNetworkClient(
        localSettings: LocalSettingsRepository,
        config: NetworkConfig = defaultNetworkConfig
    ): NetworkClient {
        val provider = httpClientProviderFactory.createSharedHttpClientProvider(config)
        return CommonDefaultKtorApiClient(provider, config, localSettings)
    }

    /**
     * A clean client that bypasses standard logic.
     * Implementation-wise, it's a "Clean" decorator/version of the network client.
     */
    fun createCleanNetworkClient(
        config: NetworkConfig = defaultNetworkConfig
    ): NetworkClient {
        val provider = httpClientProviderFactory.createSharedHttpClientProvider(config)
        return CleanNetworkClientDecorator(provider, config.isDebug)
    }

    /**
     * Specialized client for critical error reporting or fatal logs.
     */
    fun createErrorFatalLogNetworkClient(
        config: NetworkConfig = defaultNetworkConfig
    ): NetworkClient {
        val provider = httpClientProviderFactory.createStatelessHttpClientProvider(config)
        return ErrorLoggingKtorApiClient(provider, config)
    }

    /**
     * Helper to create a client for external services.
     */
    fun createCustomServiceNetworkClient(config: NetworkConfig): NetworkClient {
        val provider = httpClientProviderFactory.createSharedHttpClientProvider(config)
        return ExternalServiceKtorApiClient(provider, config)
    }
}
