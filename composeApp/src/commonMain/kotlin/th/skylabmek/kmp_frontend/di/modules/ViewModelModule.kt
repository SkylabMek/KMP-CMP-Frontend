package th.skylabmek.kmp_frontend.di.modules

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import th.skylabmek.kmp_frontend.features.feature.app.di.appFeatureModule
import th.skylabmek.kmp_frontend.features.feature.performance.di.performanceModule
import th.skylabmek.kmp_frontend.features.app_features.profile.di.profileFeatureModule
import th.skylabmek.kmp_frontend.presentation.viewmodel.MainContentViewModel

val viewModelModule = module {
    includes(performanceModule)
    includes(appFeatureModule)
    includes(profileFeatureModule)

    // MainContent ViewModel
    viewModelOf(::MainContentViewModel)

}
