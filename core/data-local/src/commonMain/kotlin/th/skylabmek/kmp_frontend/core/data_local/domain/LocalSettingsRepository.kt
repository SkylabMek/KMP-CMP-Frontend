package th.skylabmek.kmp_frontend.core.data_local.domain

import kotlinx.coroutines.flow.Flow

interface LocalSettingsRepository {
    fun getString(key: String, defaultValue: String): String
    fun setString(key: String, value: String)
    
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun setBoolean(key: String, value: Boolean)
    
    fun getInt(key: String, defaultValue: Int): Int
    fun setInt(key: String, value: Int)
    
    fun remove(key: String)
    fun clear()
    
    // Flow based for reactive UI
    fun getStringFlow(key: String, defaultValue: String): Flow<String>
    fun getBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean>
}
