package th.skylabmek.kmp_frontend.core.data_local.di

import android.content.Context
import android.content.SharedPreferences
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual fun platformModule() = module {
    single<ObservableSettings> {
        val sharedPrefs: SharedPreferences = androidContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        SharedPreferencesSettings(sharedPrefs)
    }
}
