package com.officehunter.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class AppSettings (
    val userId: Flow<String>
)
interface SettingsActions {
    suspend fun setUserId(value: Int):Preferences
}
class SettingsRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
    }

    val settings = AppSettings(
        userId = dataStore.data.map { it[USER_ID] ?: "" }
    )

    val actions = object : SettingsActions{
        override suspend fun setUserId(value: Int) = dataStore.edit { it[USER_ID] = value.toString() }
    }
}
