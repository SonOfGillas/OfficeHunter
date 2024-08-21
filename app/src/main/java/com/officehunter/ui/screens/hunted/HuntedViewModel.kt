package com.officehunter.ui.screens.hunted

import android.util.Log
import androidx.compose.ui.text.toUpperCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.officehunter.data.remote.firestore.entities.Hunted
import com.officehunter.data.remote.firestore.entities.Rarity
import com.officehunter.data.repositories.HuntedRepository
import com.officehunter.data.repositories.HuntedRepositoryData
import com.officehunter.ui.screens.profile.ProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.EnumSet
import java.util.Locale


enum class FilterOrderRule{
    DECREASING,
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
    val filterOrderRule: FilterOrderRule = FilterOrderRule.DECREASING,
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
    fun clickShowFound(checked:Boolean)
    fun clickShowNotFounded(checked:Boolean)
    fun selectOrderRule(orderRule: FilterOrderRule)
    fun selectOrderValue(orderValue: FilterOrderValue)
    fun toggleRarity(rarity:Rarity)
}

private fun Char.replaceFirstChar(any: Any) {

}

class HuntedViewModel(
    private val huntedRepository: HuntedRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HuntedState())
    val state = _state.asStateFlow()

    private val _huntedData = huntedRepository.huntedRepositoryData.asStateFlow()

    val filteredHuntedData: StateFlow<HuntedRepositoryData> = _huntedData
        .combine(_state) { data, filters ->
            val showFoundedAndNotFounded = filters.filterShowNotFounded && filters.filterShowNotFounded
            var filteredHuntedList = data.huntedList.filter { hunted ->
                /* Found Not_found filter */
                var filterFound = showFoundedAndNotFounded
                if(!showFoundedAndNotFounded){
                    if(hunted.isFoundedByCurrentUser()){
                        filterFound = filters.filterShowFounded
                    } else {
                        filterFound = filters.filterShowNotFounded
                    }
                }
                /* rarity filter */
                val filterRarity = filters.selectedRarities.contains(hunted.rarity)
                /* word filter */
                var filterWord = true
                if (filters.filterWord != ""){
                    val word = filters.filterWord.uppercase()
                    val name = hunted.name.uppercase()
                    val surname = hunted.surname.uppercase()
                    val variant = hunted.variant.uppercase()
                    filterWord = word in name || word in surname || word in variant
                }
                filterRarity && filterFound && filterWord
            }
            /* Sorting by filter */
            var sortedFilteredHuntedList = filteredHuntedList.sortedBy {
                hunted -> when(filters.filterOrderValue){
                    FilterOrderValue.RARITY -> -hunted.foundRate//rarity is the opposite of foundRate
                    FilterOrderValue.SPAWN_RATE -> hunted.spawnRate
                }
            }
            if (filters.filterOrderRule == FilterOrderRule.DECREASING){
                sortedFilteredHuntedList = sortedFilteredHuntedList.reversed()
            }
            HuntedRepositoryData(sortedFilteredHuntedList)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, HuntedRepositoryData(emptyList()))

    val actions = object : HuntedActions {
        override fun showHuntedDetails(hunted: Hunted) {
            _state.update { it.copy(selectedHunted = hunted) }
        }

        override fun closeHuntedDetailsDialog() {
            Log.d("HuntedViewModel","closeHuntedDetailDialog")
            _state.update { it.copy(selectedHunted = null) }
        }

        override fun searchHunter(word: String) {
            _state.update { it.copy(filterWord = word) }
        }

        override fun openFilterDialog() {
           _state.update { it.copy(showFilterDialog=true) }
        }

        override fun closeFilterDialog() {
            _state.update { it.copy(showFilterDialog=false) }
        }

        override fun clickShowFound(checked:Boolean) {
            _state.update { it.copy(filterShowFounded=checked) }
        }

        override fun clickShowNotFounded(checked:Boolean) {
            _state.update { it.copy(filterShowNotFounded = checked) }
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