package com.officehunter.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.officehunter.data.repositories.ProfileRepository
import com.officehunter.data.repositories.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class LoginState(
    val email: String = "",
    val password: String = ""
)

interface LoginActions {
    fun setEmail(value: String)
    fun setPassword(value: String)
    fun login()
}
class LoginViewModel (
    private val repository: UserRepository
) : ViewModel() {
    var state by mutableStateOf(LoginState("",""))
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
            viewModelScope.launch {
                repository.login(state.email,state.password)
            }
        }
    }
}

