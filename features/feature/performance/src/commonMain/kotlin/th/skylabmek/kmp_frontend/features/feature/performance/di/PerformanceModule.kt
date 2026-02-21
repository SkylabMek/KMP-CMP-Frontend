package th.skylabmek.kmp_frontend.features.feature.performance.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.viewmodel.PerformanceContentViewModel
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.viewmodel.PerformanceEditorViewModel
import th.skylabmek.kmp_frontend.features.feature.performance.presentation.viewmodel.PerformanceListViewModel

val performanceModule = module {
    viewModelOf(::PerformanceListViewModel)
    viewModelOf(::PerformanceContentViewModel)
    viewModelOf(::PerformanceEditorViewModel)
}
