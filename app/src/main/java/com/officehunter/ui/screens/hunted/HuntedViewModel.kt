package com.officehunter.ui.screens.hunted

import android.util.Log
import androidx.lifecycle.ViewModel
import com.officehunter.data.remote.firestore.entities.Hunted
import com.officehunter.data.repositories.HuntedRepository
import com.officehunter.ui.screens.profile.ProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

interface HuntedActions{

}
class HuntedViewModel(
    private val huntedRepository: HuntedRepository
) : ViewModel() {
    val huntedData = huntedRepository.huntedRepositoryData.asStateFlow()

    val actions = object : HuntedActions {
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