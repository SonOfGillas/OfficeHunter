package com.officehunter.data.repositories

import com.officehunter.data.database.dao.UserDAO
import com.officehunter.data.database.entities.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take

class UserRepository(
    private val userDAO: UserDAO,
    private val settingsRepository: SettingsRepository
) {
    val users: Flow<List<User>> = userDAO.getAllUser()

    suspend fun login(email: String, password: String){
        println("Login User Repository")
        val loginUserFlow = users.map { it.first { user -> user.email == email && user.password == password } }
        // println(loginUser.first().userId)
        try {
            val loginUser = loginUserFlow.take(1).first()
            settingsRepository.actions.setUserId(loginUser.userId)
        } catch (e:Exception){
            throw Exception("Login Failed")
        }
    }

    suspend fun getLoggedUser(): Flow<User>{
        val loginUser = users.map {
            it.first { user ->
                user.userId == settingsRepository.settings.userId.first().toInt()
            }
        }
        return loginUser
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
