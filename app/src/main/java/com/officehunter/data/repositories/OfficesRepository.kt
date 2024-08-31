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
    val favoriteOfficeId: Flow<Int?> = dataStore.data.map { preferences ->
        val favoriteOfficeIdString = preferences[FAVORITE_OFFICE_ID]?.toString()
        if (favoriteOfficeIdString.isNullOrEmpty()) null else favoriteOfficeIdString.toInt()
    }

    suspend fun setFavoriteOffice(office: Office) {
        dataStore.edit { it[FAVORITE_OFFICE_ID] = office.officeId.toString()}
    }

    suspend fun upsertOffice(office: Office){
        officeDAO.upsert(office)
    }

    suspend fun setDefaultOffices(){
        for (office in defaultOffices){
            upsertOffice(office)
        }
    }

    suspend fun removeOffice(office: Office){
        officeDAO.delete(office)
    }

    companion object {
        private val FAVORITE_OFFICE_ID = stringPreferencesKey("favorite_office_id")
    }

}


val defaultOffices = listOf(
    Office(
        officeId = 1,
        name = "San patrizio",
        street = "via g.dalle vacche 33",
        latitude = 44.495083,
        longitude = 11.832050
    ),
    Office(
        officeId = 2,
        name = "Lugo",
        street = "via piano caricatore 9",
        latitude = 44.414139,
        longitude = 11.916125
    ),
    Office(
        officeId = 3,
        name = "Imola",
        street = "via selice 51",
        latitude = 44.380724,
        longitude = 11.739033
    ),
    Office(
        officeId = 4,
        name = "Cesena University",
        street = "via cesare pavese 50",
        latitude = 44.148357,
        longitude = 12.235488
    )
)