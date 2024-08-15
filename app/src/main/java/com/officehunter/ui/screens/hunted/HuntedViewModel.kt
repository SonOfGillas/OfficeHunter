package com.officehunter.ui.screens.hunted

import android.util.Log
import androidx.lifecycle.ViewModel
import com.officehunter.data.repositories.HuntedRepository

interface HuntedActions{

}
class HuntedViewModel(
    private val huntedRepository: HuntedRepository
) : ViewModel() {
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