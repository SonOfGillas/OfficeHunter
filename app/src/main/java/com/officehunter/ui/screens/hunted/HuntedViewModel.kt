package com.officehunter.ui.screens.hunted

import android.util.Log
import androidx.lifecycle.ViewModel
import com.officehunter.data.remote.firestore.entities.Hunted
import com.officehunter.data.remote.firestore.entities.Rarity
import com.officehunter.data.repositories.HuntedRepository
import com.officehunter.data.repositories.HuntedRepositoryData
import com.officehunter.ui.screens.profile.ProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.EnumSet


enum class FilterOrderRule{
    DESCENDANT,
    INCREASING
}

enum class FilterOrderValue{
    RARITY,
    SPAWN_RATE
}

data class HuntedState(
    val selectedHunted: Hunted? = null,
    val showFilterDialog: Boolean = false,
    val filterWord: String = "",
    val filterShowFounded: Boolean = true,
    val filterShowNotFounded: Boolean = false,
    val filterOrderRule: FilterOrderRule = FilterOrderRule.DESCENDANT,
    val filterOrderValue: FilterOrderValue = FilterOrderValue.RARITY,
    val selectedRarities: Set<Rarity> = EnumSet.allOf(Rarity::class.java)

){
    val showModal = selectedHunted != null
}

interface HuntedActions{
    fun showHuntedDetails(hunted: Hunted)
    fun closeHuntedDetailsDialog()
    fun searchHunter(word:String)
    fun openFilterDialog()
    fun closeFilterDialog()
    fun toggleShowFound()
    fun toggleShowNotFounded()
    fun selectOrderRule(orderRule: FilterOrderRule)
    fun selectOrderValue(orderValue: FilterOrderValue)
    fun toggleRarity(rarity:Rarity)
}
class HuntedViewModel(
    private val huntedRepository: HuntedRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HuntedState())
    val state = _state.asStateFlow()

    private val _huntedData = huntedRepository.huntedRepositoryData.asStateFlow()
    val huntedData = _huntedData

    private fun applyFilter(){

    }

    val actions = object : HuntedActions {
        override fun showHuntedDetails(hunted: Hunted) {
            _state.update { it.copy(selectedHunted = hunted) }
        }

        override fun closeHuntedDetailsDialog() {
            Log.d("HuntedViewModel","closeHuntedDetailDialog")
            _state.update { it.copy(selectedHunted = null) }
        }

        override fun searchHunter(word: String) {
            TODO("Not yet implemented")
        }

        override fun openFilterDialog() {
           _state.update { it.copy(showFilterDialog=true) }
        }

        override fun closeFilterDialog() {
            _state.update { it.copy(showFilterDialog=false) }
        }

        override fun toggleShowFound() {
            _state.update { it.copy(filterShowFounded=!it.filterShowFounded) }
        }

        override fun toggleShowNotFounded() {
            _state.update { it.copy(filterShowNotFounded =!it.filterShowNotFounded) }
        }

        override fun selectOrderRule(orderRule: FilterOrderRule) {
           _state.update { it.copy(filterOrderRule = orderRule) }
        }

        override fun selectOrderValue(orderValue: FilterOrderValue) {
            _state.update { it.copy(filterOrderValue = orderValue) }
        }

        override fun toggleRarity(rarity: Rarity) {
            _state.update { it ->
                val newSet = it.selectedRarities.toMutableSet()
                if (newSet.contains(rarity)) {
                    newSet.remove(rarity)
                } else {
                    newSet.add(rarity)
                }
                it.copy(selectedRarities = newSet.toSet())
            }
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