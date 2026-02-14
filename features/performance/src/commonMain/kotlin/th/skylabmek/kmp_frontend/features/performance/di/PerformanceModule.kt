package th.skylabmek.kmp_frontend.features.performance.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import th.skylabmek.kmp_frontend.features.performance.presentation.viewmodel.PerformanceContentViewModel
import th.skylabmek.kmp_frontend.features.performance.presentation.viewmodel.PerformanceListViewModel

val performanceModule = module {
    viewModelOf(::PerformanceListViewModel)
    viewModelOf(::PerformanceContentViewModel)
}
