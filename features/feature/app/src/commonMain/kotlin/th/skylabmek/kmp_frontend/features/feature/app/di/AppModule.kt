package th.skylabmek.kmp_frontend.features.feature.app.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import th.skylabmek.kmp_frontend.features.feature.app.presentation.viewmodel.AppViewModel
import th.skylabmek.kmp_frontend.features.feature.app.presentation.viewmodel.AppViewModel

val appFeatureModule = module {
    viewModelOf(::AppViewModel)
}
