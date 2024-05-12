package com.officehunter.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.officehunter.data.repositories.ProfileRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class LoginState(
    val email: String = "",
    val password: String = ""
)

class LoginViewModel (
    private val repository: ProfileRepository
) : ViewModel() {
    var state by mutableStateOf(LoginState("",""))
        private set

    fun setEmail(value: String) {
        state = state.copy(email = value)
        // viewModelScope.launch { repository.setUsername(value) }
    }

    fun setPassword(value: String){
        state = state.copy(password = value)
    }
/*
    init {
        viewModelScope.launch {
            state = SettingsState(repository.username.first())
        }
    }
 */
}

