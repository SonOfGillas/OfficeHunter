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
    val errorMessage: String? = null,
)

interface ProfileActions {
    fun logout()
    fun userIsLogged():Boolean
    fun setToIdle()
}

class ProfileViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    val usersData = userRepository.userRepositoryData.asStateFlow()

    val actions = object : ProfileActions {
        override fun logout() {
           userRepository.logout()
            _state.update { it.copy(profilePhase = ProfilePhase.USER_NOT_LOGGED) }
        }

        override fun userIsLogged(): Boolean {
            return userRepository.userIsLogged()
        }

        override fun setToIdle() {
            _state.update { it.copy(profilePhase = ProfilePhase.IDLE) }
        }
    }

    init {
        viewModelScope.launch{
            _state.update { it.copy(profilePhase = ProfilePhase.LOADING) }
            userRepository.updateData{
                result ->
                    result.onFailure {
                        _state.update { it.copy(profilePhase = ProfilePhase.ERROR, errorMessage = it.errorMessage) }
                    }
            }
        }
    }
}