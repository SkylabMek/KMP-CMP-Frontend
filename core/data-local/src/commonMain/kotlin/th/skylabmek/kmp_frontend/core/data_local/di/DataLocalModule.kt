package th.skylabmek.kmp_frontend.core.data_local.di

import com.russhwolf.settings.ObservableSettings
import org.koin.core.module.Module
import org.koin.dsl.module
import th.skylabmek.kmp_frontend.core.data_local.domain.LocalSettingsRepository
import th.skylabmek.kmp_frontend.core.data_local.repository.MultiplatformSettingsRepository

expect fun platformModule(): Module

object DataLocalModule {
    fun modules() = module {
        includes(platformModule())
        single<LocalSettingsRepository> { MultiplatformSettingsRepository(get()) }
    }
}
