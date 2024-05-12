package com.officehunter.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.officehunter.data.database.dao.UserDAO
import com.officehunter.data.database.entities.User

@Database(entities = [Place::class, User::class], version = 3)
abstract class TravelDiaryDatabase : RoomDatabase() {
    abstract fun placesDAO(): PlacesDAO
    abstract fun userDAO(): UserDAO
}
