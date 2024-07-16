package com.officehunter.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.officehunter.data.remote.FirebaseAuth

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
    private val authRepository: FirebaseAuth
) : ViewModel() {
    var state by mutableStateOf(LoginState())
        private set

    val actions = object : LoginActions {
        override fun setEmail(value: String) {
            state = state.copy(email = value)
            // viewModelScope.launch { repository.setUsername(value) }
        }

        override fun setPassword(value: String) {
            state = state.copy(password = value)
        }

        override fun login(){
            println("Login View Model")
            state = state.copy(loginPhase = LoginPhase.LOADING)
            /*
            viewModelScope.launch {
                try {
                    repository.login(state.email,state.password)
                    state = state.copy(loginPhase = LoginPhase.LOGGED)
                } catch (e:Exception){
                    println(e.message)
                    state = state.copy(
                        loginPhase = LoginPhase.ERROR,
                        errorMessage = e.message?:"Login Failed"
                    )
                }
            }
            */
            authRepository.login(state.email, state.password) { result ->
                result.onSuccess {
                    state = state.copy(loginPhase = LoginPhase.LOGGED)
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
            return authRepository.userIsLogged()
        }
    }

    init {
        if (actions.userIsLogged()){
            state = state.copy(loginPhase = LoginPhase.LOGGED)
        }
    }
}

