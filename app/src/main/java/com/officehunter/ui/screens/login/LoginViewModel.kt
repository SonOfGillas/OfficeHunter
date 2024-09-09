package com.officehunter.ui.screens.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.officehunter.data.remote.FirebaseAuthRemote
import com.officehunter.data.remote.firestore.entities.User
import com.officehunter.data.repositories.UserRepository

enum class LoginPhase {
    IDLE,
    LOADING,
    LOGGED,
    ERROR,
}

data class LoginState(
    val loginPhase: LoginPhase = LoginPhase.IDLE,
    val email: String = "",
    val password: String = "",
    val errorMessage: String = "",
    val onBoardingCompleted: Boolean = false
){
    fun hasError():Boolean {
        return loginPhase == LoginPhase.ERROR;
    }
}

interface LoginActions {
    fun setEmail(value: String)
    fun setPassword(value: String)
    fun login()
    fun setToIdle()
    fun userIsLogged():Boolean
}
class LoginViewModel (
    private val userRepository: UserRepository
) : ViewModel() {
    var state by mutableStateOf(LoginState())
        private set

    val actions = object : LoginActions {
        override fun setEmail(value: String) {
            state = state.copy(email = value)
        }

        override fun setPassword(value: String) {
            state = state.copy(password = value)
        }

        override fun login(){
            state = state.copy(loginPhase = LoginPhase.LOADING)
            userRepository.login(state.email, state.password) { result ->
                result.onSuccess {
                    state = state.copy(loginPhase = LoginPhase.LOGGED, onBoardingCompleted=userRepository.userRepositoryData.value.onBoardingCompleted )
                }.onFailure {
                    state = state.copy(
                        loginPhase = LoginPhase.ERROR,
                        errorMessage = it.localizedMessage?:"Unknown Error"
                    )
                }
            }
        }

        override fun setToIdle(){
            state = state.copy(
                loginPhase = LoginPhase.IDLE,
                errorMessage = ""
            )
        }

        override fun userIsLogged():Boolean {
            return userRepository.userIsLogged()
        }
    }

    init {
        userRepository.updateData {
            it.onSuccess {
                if(userRepository.userRepositoryData.value.currentUser != null){
                    state = state.copy(
                        loginPhase = LoginPhase.LOGGED,
                        onBoardingCompleted=userRepository.userRepositoryData.value.onBoardingCompleted
                        )
                }
            }
        }

    }
}

