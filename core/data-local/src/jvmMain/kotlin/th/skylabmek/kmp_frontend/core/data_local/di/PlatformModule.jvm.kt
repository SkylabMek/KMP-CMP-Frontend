package th.skylabmek.kmp_frontend.core.data_local.di

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import org.koin.dsl.module
import java.util.prefs.Preferences

actual fun platformModule() = module {
    single<ObservableSettings> {
        val preferences = Preferences.userRoot().node("th.skylabmek.kmp_frontend.settings")
        PreferencesSettings(preferences)
    }
}
