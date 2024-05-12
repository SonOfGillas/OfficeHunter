package com.officehunter.data.repositories

import com.officehunter.data.database.dao.UserDAO
import com.officehunter.data.database.entities.User
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDAO: UserDAO,
) {
    val users: Flow<List<User>> = userDAO.getAllUser()

    suspend fun newUser(
        name: String,
        surname: String,
        email: String,
        password: String
    ){
        userDAO.upsert(
            User(
                name = name,
                surname = surname,
                email = email,
                password = password
            )
        )
    }
}
