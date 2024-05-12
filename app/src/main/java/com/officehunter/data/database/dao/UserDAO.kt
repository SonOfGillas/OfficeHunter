package com.officehunter.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.officehunter.data.database.Place
import com.officehunter.data.database.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDAO {
    @Query("SELECT * FROM user")
    fun getAllUser(): Flow<List<User>>

    @Query("SELECT * FROM user WHERE userId= :id")
    fun getUserById(id: Int): User

    @Upsert
    suspend fun upsert(user: User)

    @Delete
    suspend fun delete(item: User)
}