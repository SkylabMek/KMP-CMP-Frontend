package th.skylabmek.kmp_frontend.core.data_local.repository

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.Flow
import th.skylabmek.kmp_frontend.core.data_local.domain.LocalSettingsRepository

class MultiplatformSettingsRepository(private val settings: ObservableSettings) : LocalSettingsRepository {
    
    @OptIn(ExperimentalSettingsApi::class)
    private val flowSettings by lazy { settings.toFlowSettings() }

    override fun getString(key: String, defaultValue: String): String {
        return settings[key] ?: defaultValue
    }

    override fun setString(key: String, value: String) {
        settings[key] = value
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return settings[key] ?: defaultValue
    }

    override fun setBoolean(key: String, value: Boolean) {
        settings[key] = value
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return settings[key] ?: defaultValue
    }

    override fun setInt(key: String, value: Int) {
        settings[key] = value
    }

    override fun remove(key: String) {
        settings.remove(key)
    }

    override fun clear() {
        settings.clear()
    }

    override fun getStringFlow(key: String, defaultValue: String): Flow<String> {
        return flowSettings.getStringFlow(key, defaultValue)
    }

    override fun getBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean> {
        return flowSettings.getBooleanFlow(key, defaultValue)
    }
}
