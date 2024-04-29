package com.officehunter.data.repositories

import android.content.ContentResolver
import android.net.Uri
import com.officehunter.utils.saveImageToStorage
import com.officehunter.data.database.Place
import com.officehunter.data.database.PlacesDAO
import kotlinx.coroutines.flow.Flow

class PlacesRepository(
    private val placesDAO: PlacesDAO,
    private val contentResolver: ContentResolver
) {
    val places: Flow<List<Place>> = placesDAO.getAll()

    suspend fun upsert(place: Place) {
        if (place.imageUri?.isNotEmpty() == true) {
            val imageUri = saveImageToStorage(
                Uri.parse(place.imageUri),
                contentResolver,
                "TravelDiary_Place${place.name}"
            )
            placesDAO.upsert(place.copy(imageUri = imageUri.toString()))
        } else {
            placesDAO.upsert(place)
        }
    }

    suspend fun delete(place: Place) = placesDAO.delete(place)
}
