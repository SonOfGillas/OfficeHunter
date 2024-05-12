package com.officehunter.data.repositories

import com.officehunter.data.database.dao.UserDAO
import com.officehunter.data.database.entities.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class UserRepository(
    private val userDAO: UserDAO,
    private val settingsRepository: SettingsRepository
) {
    val users: Flow<List<User>> = userDAO.getAllUser()

    suspend fun login(email: String, password: String){
        val loginUser = users.map { it.filter { user -> user.email == email && user.password == password } }.first()
        settingsRepository.actions.setUserId(loginUser.first().userId)
        println(settingsRepository.settings.userId)
        loginUser;
    }

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
