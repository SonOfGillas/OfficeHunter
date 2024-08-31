package com.officehunter.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.officehunter.data.database.dao.UserDAO
import com.officehunter.data.database.entities.User

@Database(entities = [Office::class, Achievement::class, Place::class, User::class], version = 9)
abstract class TravelDiaryDatabase : RoomDatabase() {
    abstract fun officeDAO():OfficeDAO
    abstract fun achievementDA0():AchievementDA0
    abstract fun placesDAO(): PlacesDAO
    abstract fun userDAO(): UserDAO
}
