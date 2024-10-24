package com.officehunter.ui.screens.profile

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.auth.User
import com.officehunter.data.remote.FirebaseAuthRemote
import com.officehunter.data.repositories.AchievementRepository
import com.officehunter.data.repositories.ImageRepository
import com.officehunter.data.repositories.SettingsRepository
import com.officehunter.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
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
    suspend fun getAchievementsIcon(imageName: String): Uri?
    fun  onChangeTheme()
}

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository,
    private val achievementRepository: AchievementRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    val usersData = userRepository.userRepositoryData.asStateFlow()
    val achievements = achievementRepository.achievements.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )
    private val _settings = settingsRepository.settings
    val settings = _settings.asStateFlow()

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

        override suspend fun getAchievementsIcon(imageName: String): Uri? {
           return imageRepository.getAchievementImage(imageName)
        }

        override fun onChangeTheme(){
            viewModelScope.launch{
                settingsRepository.actions.setIsDarkTheme(!_settings.value.isDarkTheme)
            }
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