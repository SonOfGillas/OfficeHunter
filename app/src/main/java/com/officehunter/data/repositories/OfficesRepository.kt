package com.officehunter.data.repositories


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.officehunter.data.database.Office
import com.officehunter.data.database.OfficeDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class OfficesRepository(
    private val dataStore: DataStore<Preferences>,
    private val officeDAO: OfficeDAO,
) {
    val offices: Flow<List<Office>> = officeDAO.getAll()
    val favoriteOfficeId: Flow<Int> = dataStore.data.map{ (it[FAVORITE_OFFICE_ID] ?: "").toInt() }

    suspend fun setFavoriteOffice(office: Office) {
        dataStore.edit { it[FAVORITE_OFFICE_ID] = office.officeId.toString()}
    }

    companion object {
        private val FAVORITE_OFFICE_ID = stringPreferencesKey("favorite_office_id")
    }
}
