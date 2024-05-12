package com.officehunter.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User (
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,

    @ColumnInfo
    val name: String,

    @ColumnInfo
    val surname: String,

    @ColumnInfo
    val points: Int = 0,

    @ColumnInfo
    val coffee: Int = 0,

    @ColumnInfo
    val email: String,

    @ColumnInfo
    val password: String
)
