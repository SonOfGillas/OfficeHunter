package com.officehunter.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.officehunter.data.remote.FirebaseAuth
import com.officehunter.ui.screens.questions.QuestionsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ProfilePhase {
    LOADING,
    IDLE,
    USER_NOT_LOGGED,
    ERROR,
}

data class ProfileState(
    val profilePhase: ProfilePhase = ProfilePhase.IDLE,
    val userName: String = "Satoshi Nakamoto" //TODO update user
)

interface ProfileActions {
    fun logout()
    fun userIsLogged():Boolean
}

class ProfileViewModel(
    private val authRepository: FirebaseAuth
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    val actions = object : ProfileActions {
        override fun logout() {
            authRepository.logout()
            _state.update { it.copy(profilePhase = ProfilePhase.USER_NOT_LOGGED) }
        }

        override fun userIsLogged(): Boolean {
            return authRepository.userIsLogged()
        }
    }
}