package th.skylabmek.kmp_frontend.core.data_local.di

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual fun platformModule() = module {
    single<ObservableSettings> {
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
    }
}
