package th.skylabmek.kmp_frontend.di.modules

import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import th.skylabmek.kmp_frontend.core.common.AppConfig
import th.skylabmek.kmp_frontend.features.feature.app.di.appFeatureModule
import th.skylabmek.kmp_frontend.features.feature.performance.di.performanceModule
import th.skylabmek.kmp_frontend.features.app_features.profile.di.profileFeatureModule
import th.skylabmek.kmp_frontend.presentation.viewmodel.AppInitViewModel
import th.skylabmek.kmp_frontend.presentation.viewmodel.MainContentViewModel

val viewModelModule = module {
    // application ViewModel
    viewModel {
        AppInitViewModel(
            configRepository = get(),
            localSettings = get(),
            appId = AppConfig.APP_ID
        )
    }
    viewModelOf(::MainContentViewModel)

    includes(performanceModule)
    includes(appFeatureModule)
    includes(profileFeatureModule)
}
