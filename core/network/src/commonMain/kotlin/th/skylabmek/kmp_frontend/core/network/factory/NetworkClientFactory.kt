package th.skylabmek.kmp_frontend.core.network.factory

import th.skylabmek.kmp_frontend.core.data_local.domain.LocalSettingsRepository
import th.skylabmek.kmp_frontend.core.network.config.NetworkConfig
import th.skylabmek.kmp_frontend.core.network.network_client.impl.CommonDefaultKtorApiClient
import th.skylabmek.kmp_frontend.core.network.network_client.impl.ErrorLoggingKtorApiClient
import th.skylabmek.kmp_frontend.core.network.network_client.impl.ExternalServiceKtorApiClient
import th.skylabmek.kmp_frontend.core.network.network_client.NetworkClient

class NetworkClientFactory(
    private val defaultNetworkConfig: NetworkConfig,
    private val httpClientProviderFactory: HttpClientProviderFactory
) {
    /**
     * Standard client for feature-related API calls.
     * Uses a SHARED provider to optimize connections and resource usage.
     */
    fun createDefaultFeatureNetworkClient(
        localSettings: LocalSettingsRepository,
        config: NetworkConfig = defaultNetworkConfig
    ): NetworkClient {
        val provider = httpClientProviderFactory.createSharedHttpClientProvider(config)
        return CommonDefaultKtorApiClient(provider, config, localSettings)
    }

    /**
     * Specialized client for critical error reporting or fatal logs.
     * Uses a STATELESS provider and a specialized error-resilient client.
     */
    fun createErrorFatalLogNetworkClient(
        config: NetworkConfig = defaultNetworkConfig
    ): NetworkClient {
        val provider = httpClientProviderFactory.createStatelessHttpClientProvider(config)
        return ErrorLoggingKtorApiClient(provider, config)
    }

    /**
     * Helper to create a client for external services (e.g., Payment, Maps).
     * Uses a dedicated configuration and handles URL resolution specifically for external APIs.
     */
    fun createCustomServiceNetworkClient(config: NetworkConfig): NetworkClient {
        val provider = httpClientProviderFactory.createSharedHttpClientProvider(config)
        return ExternalServiceKtorApiClient(provider, config)
    }
}
