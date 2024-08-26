package com.officehunter.ui.screens.hunt

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.officehunter.data.remote.firestore.entities.Hunted
import com.officehunter.data.repositories.HuntedRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.random.Random

data class HuntState(
    val spawnedHunted: List<Hunted> = emptyList()
)

data class HuntData(
    val huntedList: List<Hunted> = emptyList(),
    val weightedHuntedList: List<Hunted> = emptyList()
)

interface HuntActions{

}
class HuntViewModel(
    private val huntedRepository: HuntedRepository,
) : ViewModel() {

    private val lastSpawnTime = huntedRepository.lastSpawnTime.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Date(0)
    )

    private val _state = MutableStateFlow(HuntState())
    val state = _state.asStateFlow()
    
    private val huntData: StateFlow<HuntData> = huntedRepository.huntedRepositoryData.map{
        HuntData(
            huntedList = it.huntedList,
            weightedHuntedList = it.huntedList.flatMap { hunted ->
                List(hunted.extractionWeight) { hunted.copy() }
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = HuntData()
    )

    val actions = object : HuntActions {
    }

    init {
        huntedRepository.updateData {
                result ->
            result.onSuccess {
                Log.d("HuntViewModel", "hunted list ${huntedRepository.huntedRepositoryData.value.huntedList}")
            }
            result.onFailure {
                it.message?.let { it1 -> Log.d("HuntViewModel", it1) }
            }
        }

        viewModelScope.launch {
            lastSpawnTime.collect {
                huntData.collect{
                    while (true) {
                        val currentTime = Date()
                        val timeDifference = currentTime.time - lastSpawnTime.value.time
                        val weightedHuntedList = huntData.value.weightedHuntedList
                        if (timeDifference > SPAWN_PERIOD && weightedHuntedList.isNotEmpty()) {
                            val randomIndex = Random.nextInt(weightedHuntedList.size)
                            val randomHunted = weightedHuntedList[randomIndex]
                            Log.d("HuntViewModel", "randomHunted ${randomHunted.surname}")
                            runBlocking {
                                huntedRepository.setLastSpawnTime()
                            }
                            if(state.value.spawnedHunted.size >= 10){
                                val currentList = _state.value.spawnedHunted
                                _state.value = _state.value.copy(
                                    spawnedHunted = currentList.subList(1,currentList.size) + randomHunted
                                )
                            } else {
                                _state.value = _state.value.copy(
                                    spawnedHunted = _state.value.spawnedHunted + randomHunted
                                )
                            }
                        }
                        delay(SPAWN_PERIOD) // Check every second
                        Log.d("HuntViewModel", state.value.spawnedHunted.size.toString())
                    }
                }
            }
        }
    }

    companion object{
        val SPAWN_PERIOD = TimeUnit.SECONDS.toMillis(10)
    }
}