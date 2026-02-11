package th.skylabmek.kmp_frontend.core.data_local.di

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import com.russhwolf.settings.SettingsListener
import org.koin.dsl.module
import kotlinx.browser.window

class JsStorageSettings(private val delegate: StorageSettings) : ObservableSettings, Settings by delegate {
    override fun addIntListener(key: String, defaultValue: Int, callback: (Int) -> Unit): SettingsListener {
        return object : SettingsListener { override fun deactivate() {} }
    }
    override fun addLongListener(key: String, defaultValue: Long, callback: (Long) -> Unit): SettingsListener {
        return object : SettingsListener { override fun deactivate() {} }
    }
    override fun addStringListener(key: String, defaultValue: String, callback: (String) -> Unit): SettingsListener {
        return object : SettingsListener { override fun deactivate() {} }
    }
    override fun addFloatListener(key: String, defaultValue: Float, callback: (Float) -> Unit): SettingsListener {
        return object : SettingsListener { override fun deactivate() {} }
    }
    override fun addDoubleListener(key: String, defaultValue: Double, callback: (Double) -> Unit): SettingsListener {
        return object : SettingsListener { override fun deactivate() {} }
    }
    override fun addBooleanListener(key: String, defaultValue: Boolean, callback: (Boolean) -> Unit): SettingsListener {
        return object : SettingsListener { override fun deactivate() {} }
    }

    override fun addBooleanOrNullListener(key: String, callback: (Boolean?) -> Unit): SettingsListener {
        return object : SettingsListener { override fun deactivate() {} }
    }
    override fun addDoubleOrNullListener(key: String, callback: (Double?) -> Unit): SettingsListener {
        return object : SettingsListener { override fun deactivate() {} }
    }
    override fun addFloatOrNullListener(key: String, callback: (Float?) -> Unit): SettingsListener {
        return object : SettingsListener { override fun deactivate() {} }
    }
    override fun addIntOrNullListener(key: String, callback: (Int?) -> Unit): SettingsListener {
        return object : SettingsListener { override fun deactivate() {} }
    }
    override fun addLongOrNullListener(key: String, callback: (Long?) -> Unit): SettingsListener {
        return object : SettingsListener { override fun deactivate() {} }
    }
    override fun addStringOrNullListener(key: String, callback: (String?) -> Unit): SettingsListener {
        return object : SettingsListener { override fun deactivate() {} }
    }
}

actual fun platformModule() = module {
    single<ObservableSettings> {
        JsStorageSettings(StorageSettings(window.localStorage))
    }
}
