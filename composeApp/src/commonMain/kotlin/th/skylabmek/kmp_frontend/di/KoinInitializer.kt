package th.skylabmek.kmp_frontend.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import th.skylabmek.kmp_frontend.core.data_local.di.DataLocalModule
import th.skylabmek.kmp_frontend.di.modules.appNetworkModule
import th.skylabmek.kmp_frontend.di.modules.repositoryModule
import th.skylabmek.kmp_frontend.di.modules.viewModelModule

/**
 * Initialize Koin dependency injection.
 * Call this function before using any dependencies.
 * 
 * @param appDeclaration Optional platform-specific Koin configuration
 */
fun initKoin(appDeclaration: KoinAppDeclaration? = null) {
    startKoin {
        appDeclaration?.invoke(this)
        modules(
            appNetworkModule,
            DataLocalModule.modules(),
            repositoryModule,
            viewModelModule,
        )
    }
}
