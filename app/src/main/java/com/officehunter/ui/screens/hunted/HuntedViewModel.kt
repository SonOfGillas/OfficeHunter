package com.officehunter.ui.screens.hunted

import android.util.Log
import androidx.lifecycle.ViewModel
import com.officehunter.data.remote.firestore.entities.Hunted
import com.officehunter.data.repositories.HuntedRepository
import com.officehunter.ui.screens.profile.ProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HuntedState(
    val selectedHunted: Hunted? = null,
){
    val showModal = selectedHunted != null
}

interface HuntedActions{
    fun showHuntedDetails(hunted: Hunted)
    fun closeHuntedDetailsDialog()
}
class HuntedViewModel(
    private val huntedRepository: HuntedRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HuntedState())
    val state = _state.asStateFlow()

    val huntedData = huntedRepository.huntedRepositoryData.asStateFlow()

    val actions = object : HuntedActions {
        override fun showHuntedDetails(hunted: Hunted) {
            _state.update { it.copy(selectedHunted = hunted) }
        }

        override fun closeHuntedDetailsDialog() {
            Log.d("HuntedViewModel","closeHuntedDetailDialog")
            _state.update { it.copy(selectedHunted = null) }
        }

    }

    init {
        huntedRepository.updateData {
            result ->
                result.onSuccess {
                    Log.d("HuntedViewModel", huntedRepository.huntedRepositoryData.value.huntedList.toString())
                }
                result.onFailure {
                    it.message?.let { it1 -> Log.d("HuntedViewModel", it1) }
                }
        }
    }
}