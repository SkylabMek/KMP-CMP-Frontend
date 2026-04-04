package th.skylabmek.kmp_frontend.features.app_features.profile.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import th.skylabmek.kmp_frontend.features.app_features.profile.presentation.viewmodel.ProfilePublicViewModel
import th.skylabmek.kmp_frontend.features.app_features.profile.presentation.viewmodel.ProfileViewModel

val profileFeatureModule = module {
    viewModelOf(::ProfileViewModel)
    viewModelOf(::ProfilePublicViewModel)
}
