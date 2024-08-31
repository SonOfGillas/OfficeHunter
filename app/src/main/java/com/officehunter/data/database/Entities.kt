package com.officehunter.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Place (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo
    val name: String,

    @ColumnInfo
    val date: String,

    @ColumnInfo
    val description: String,

    @ColumnInfo
    val imageUri: String?
)

@Entity(tableName = "office")
class Office (
    @PrimaryKey(autoGenerate = true)
    val officeId: Int = 0,

    @ColumnInfo
    val name: String,

    @ColumnInfo
    val street: String,

    @ColumnInfo
    val latitude: Double,

    @ColumnInfo
    val longitude: Double,
)

@Entity(tableName = "achievement")
class Achievement(
    @PrimaryKey
    val id: Int,
    @ColumnInfo
    val name: String,
    @ColumnInfo
    val description: String,
    @ColumnInfo
    val imageName: String,
    @ColumnInfo
    val pointValue: Int,
    @ColumnInfo
    val numberOfTimesAchieved: Int
)