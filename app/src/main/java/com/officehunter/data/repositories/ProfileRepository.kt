package com.officehunter.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map

class ProfileRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val EMAIL_KEY = stringPreferencesKey("email")
    }

    val email = dataStore.data.map { it[EMAIL_KEY] ?: "" }

    suspend fun setUsername(value: String) = dataStore.edit { it[EMAIL_KEY] = value }
}