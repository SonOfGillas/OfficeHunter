package com.officehunter.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppSettings (
    val isDarkTheme: Boolean = false
)
interface SettingsActions {
    suspend fun setIsDarkTheme(value: Boolean):Preferences
}
class SettingsRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val IS_DARK_THEME = stringPreferencesKey("is_dark_theme")
    }

    val settings = MutableStateFlow(
        AppSettings()
    )

    val actions = object : SettingsActions{
        override suspend fun setIsDarkTheme(value: Boolean) = dataStore.edit { it[IS_DARK_THEME] =
            value.toString()
        }
    }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.data.map { it[IS_DARK_THEME]?.toBoolean() }.collect { isDarkTheme ->
                settings.update { it.copy(isDarkTheme = isDarkTheme ?: false) }
            }
        }
    }
}
