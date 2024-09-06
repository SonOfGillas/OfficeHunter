package com.officehunter.data.repositories


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.officehunter.data.database.Office
import com.officehunter.data.database.OfficeDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.map
import org.osmdroid.util.Distance
import org.osmdroid.util.GeoPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class NearestOffice(
    val office: Office,
    val distanceKm: Double
)

class OfficesRepository(
    private val dataStore: DataStore<Preferences>,
    private val officeDAO: OfficeDAO,
) {
    val offices: Flow<List<Office>> = officeDAO.getAll()
    val favoriteOfficeId: Flow<Int?> = dataStore.data.map { preferences ->
        val favoriteOfficeIdString = preferences[FAVORITE_OFFICE_ID]?.toString()
        if (favoriteOfficeIdString.isNullOrEmpty()) null else favoriteOfficeIdString.toInt()
    }

    private fun calculateDistance(point1: GeoPoint, point2: GeoPoint): Double {
        val latDistance = Math.toRadians(point1.latitude - point2.latitude)
        val lonDistance = Math.toRadians(point1.longitude - point2.longitude)
        val a = sin(latDistance / 2) * sin(latDistance / 2) +
                cos(Math.toRadians(point1.latitude)) * cos(Math.toRadians(point2.latitude)) *
                sin(lonDistance / 2) * sin(lonDistance / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val radiusOfEarth = 6371.0 // Radius of the earth in kilometers
        return radiusOfEarth * c
    }

    suspend fun getNearestOffice(geoPoint: GeoPoint):NearestOffice?{
        val nearestOffice = offices.first().minByOrNull { calculateDistance(geoPoint, GeoPoint(it.latitude, it.longitude)) }
        if(nearestOffice!=null){
            return NearestOffice(
                office=nearestOffice,
                distanceKm = calculateDistance(geoPoint,GeoPoint(nearestOffice.latitude, nearestOffice.longitude))
            )
        } else {
            return null
        }
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
    ),
    Office(
        officeId = 5,
        name = "Google",
        street = "1600 Amphitheatre Pkwy",
        latitude = 37.422377,
        longitude = -122.083797
    )
)