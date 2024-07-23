package com.officehunter.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.auth.User
import com.officehunter.data.remote.FirebaseAuthRemote
import com.officehunter.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ProfilePhase {
    LOADING,
    IDLE,
    USER_NOT_LOGGED,
    ERROR,
}

data class ProfileState(
    val profilePhase: ProfilePhase = ProfilePhase.IDLE,
    val userName: String = "Satoshi Nakamoto", //TODO update user
)

interface ProfileActions {
    fun logout()
    fun userIsLogged():Boolean
}

class ProfileViewModel(
    private val authRepository: FirebaseAuthRemote,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    val userList = userRepository.usersList

    val actions = object : ProfileActions {
        override fun logout() {
            authRepository.logout()
            _state.update { it.copy(profilePhase = ProfilePhase.USER_NOT_LOGGED) }
        }

        override fun userIsLogged(): Boolean {
            return authRepository.userIsLogged()
        }
    }

    init {
        viewModelScope.launch{
            userRepository.getUsers()
        }
    }
}