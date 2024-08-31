package com.officehunter.ui.screens.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.officehunter.data.database.Achievement
import com.officehunter.data.database.Office
import com.officehunter.data.repositories.AchievementRepository
import com.officehunter.data.repositories.OfficesRepository
import com.officehunter.ui.screens.offices.OfficesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

enum class SplashState{
    Loading,
    Ready
}

class SplashViewModel(
    officesRepository: OfficesRepository,
    achievementRepository: AchievementRepository
): ViewModel(){
    private val _state = MutableStateFlow(SplashState.Loading)
    val state = _state.asStateFlow()

    private val offices:StateFlow<List<Office>> = officesRepository.offices.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    private  val achievements:StateFlow<List<Achievement>> = achievementRepository.achievements.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            withTimeout(1000){
                if (offices.value.isEmpty()){
                    Log.d("SplashViewModel","defaultOffices")
                    officesRepository.setDefaultOffices()
                }
                if (achievements.value.isEmpty()){
                    Log.d("SplashViewModel","defaultAchievemets")
                    achievementRepository.setDefaultAchievement()
                }
                _state.update { SplashState.Ready }
            }
        }
    }
}

