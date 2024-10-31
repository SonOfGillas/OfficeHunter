package com.officehunter.ui.screens.profile

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.auth.User
import com.officehunter.data.remote.FirebaseAuthRemote
import com.officehunter.data.remote.firestore.entities.Hunted
import com.officehunter.data.repositories.AchievementRepository
import com.officehunter.data.repositories.HuntedRepository
import com.officehunter.data.repositories.HuntedRepositoryData
import com.officehunter.data.repositories.ImageRepository
import com.officehunter.data.repositories.SettingsRepository
import com.officehunter.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
    fun userIsLogged(): Boolean
    fun setToIdle()
    suspend fun getAchievementsIcon(imageName: String): Uri?
    suspend fun getHuntedImage(hunted: Hunted): Uri?
    fun onChangeTheme()
}

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val imageRepository: ImageRepository,
    private val achievementRepository: AchievementRepository,
    private val settingsRepository: SettingsRepository,
    private val huntedRepository: HuntedRepository,
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
    val userProfileIconHunted = huntedRepository.huntedRepositoryData.asStateFlow().combine(usersData){
        huntedData, usersData ->
            if(usersData.currentUser!=null){
                val userHunteds = huntedData.huntedList.filter { hunted -> hunted.isOwner(usersData.currentUser) }
                if(userHunteds.isNotEmpty()){
                    return@combine userHunteds.first()
                } else {
                    return@combine null
                }
            } else {
                return@combine null
            }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)




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

        override suspend fun getHuntedImage(hunted: Hunted): Uri? {
            return imageRepository.getHuntedImage(hunted.id)
        }

        override fun onChangeTheme() {
            viewModelScope.launch {
                settingsRepository.actions.setIsDarkTheme(!_settings.value.isDarkTheme)
            }
        }
    }

    init {
        viewModelScope.launch {
            _state.update { it.copy(profilePhase = ProfilePhase.LOADING) }
            // this function update also the user data
            huntedRepository.updateData { result ->
                result.onFailure {
                    _state.update {
                        it.copy(
                            profilePhase = ProfilePhase.ERROR,
                            errorMessage = it.errorMessage
                        )
                    }
                }
            }
        }
    }
}