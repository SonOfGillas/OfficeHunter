package com.officehunter.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.officehunter.data.remote.firestore.entities.User
import com.officehunter.data.repositories.HuntedRepository
import com.officehunter.data.repositories.HuntedRepositoryData
import com.officehunter.data.repositories.UserRepository
import com.officehunter.data.repositories.UserRepositoryData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn


data class StatsState(
    val currentUser: User? = null,
    val usersRank: List<User> = emptyList(),
    val totalHunted: Int = 0,
    val huntedFounded: Int = 0
)

interface StatsActions{

}
class StatsViewModel(
    userRepository: UserRepository,
    huntedRepository: HuntedRepository,
) : ViewModel() {

    val state: StateFlow<StatsState> = combine(
        userRepository.userRepositoryData,
        huntedRepository.huntedRepositoryData
    ){
        userRepositoryData, huntedRepositoryData ->
        StatsState(
            currentUser = userRepositoryData.currentUser,
            usersRank = userRepositoryData.usersList.sortedByDescending { it.points.toInt() },
            totalHunted = huntedRepositoryData.huntedList.size,
            huntedFounded = huntedRepositoryData.huntedList.filter { it.isFoundedByCurrentUser() }.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = StatsState()
    )


    val actions = object : StatsActions {
    }
}