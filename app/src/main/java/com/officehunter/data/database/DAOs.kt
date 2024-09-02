package com.officehunter.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PlacesDAO {
    @Query("SELECT * FROM place ORDER BY name ASC")
    fun getAll(): Flow<List<Place>>

    @Upsert
    suspend fun upsert(place: Place)

    @Delete
    suspend fun delete(item: Place)
}

@Dao
interface OfficeDAO {
    @Query("SELECT * FROM office ORDER BY name ASC")
    fun getAll(): Flow<List<Office>>

    @Upsert
    suspend fun upsert(office: Office)

    @Delete
    suspend fun delete(item: Office)
}

@Dao
interface AchievementDA0{
    @Query("SELECT * FROM achievement ORDER BY id ASC")
    fun getAll(): Flow<List<Achievement>>

    @Upsert
    suspend fun upsert(achievement: Achievement)

    @Delete
    suspend fun delete(item: Achievement)

}